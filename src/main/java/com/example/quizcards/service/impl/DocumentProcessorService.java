package com.example.quizcards.service.impl;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringJoiner;

@Service
@Slf4j
public class DocumentProcessorService {

    private final Tika tika = new Tika();

    @Cacheable(cacheNames = "extractedText", value = "extractedText", key = "#file.originalFilename + #file.size")
    public String extractTextFromDocument(MultipartFile file, String pdfPassword) throws IOException {
        try {
            String mimeType = tika.detect(file.getInputStream());
            log.info("Detected MIME type: {}", mimeType);

            // Kiểm tra cả hai định dạng Excel phổ biến
            if (mimeType.equals("application/pdf")) {
                return extractTextFromPdf(file.getInputStream(), pdfPassword);
            } else if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return extractTextFromWord(file.getInputStream());
            } else if (mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                    mimeType.equals("application/vnd.ms-excel")) {
                return extractTextFromExcel(file.getInputStream());
            } else {
                // Thử với Tika cho tất cả các định dạng khác
                return extractWithTika(file.getInputStream());
            }
        } catch (Exception e) {
            log.error("Error extracting text from document: {}", e.getMessage(), e);
            throw new IOException("Không thể trích xuất văn bản: " + e.getMessage(), e);
        }
    }

    private String extractTextFromWord(InputStream inputStream) throws IOException {
        log.debug("Extracting text from Word document");
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String extractTextFromPdf(InputStream inputStream, String password) throws IOException {
        log.debug("Extracting text from PDF document");

        // Nếu có mật khẩu, dùng iText để trích xuất
        if (password != null && !password.isEmpty()) {
            return extractTextFromProtectedPdf(inputStream, password);
        }

        // Không có mật khẩu, thử với PDFBox trước
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            log.warn("PDFBox failed to extract text, trying with Tika", e);
            // Nếu PDFBox không trích xuất được, thử với Tika
            try {
                return extractWithTika(inputStream);
            } catch (Exception tikaEx) {
                log.error("Tika also failed", tikaEx);
                throw new IOException("Không thể trích xuất văn bản từ PDF. File có thể bị hỏng hoặc được bảo vệ bằng mật khẩu.", e);
            }
        }
    }

    private String extractTextFromProtectedPdf(InputStream inputStream, String password) throws IOException {
        log.debug("Extracting text from password-protected PDF");
        StringBuilder text = new StringBuilder();
        try {
            PdfReader reader = new PdfReader(inputStream, password.getBytes());
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                text.append(PdfTextExtractor.getTextFromPage(reader, i));
                text.append("\n");
            }
            reader.close();
            return text.toString();
        } catch (Exception e) {
            log.error("Error extracting text from protected PDF", e);
            throw new IOException("Không thể trích xuất văn bản từ PDF được bảo vệ. Mật khẩu có thể không đúng.", e);
        }
    }

    private String extractTextFromExcel(InputStream inputStream) throws IOException {
        log.debug("Extracting text from Excel document");

        try {
            // Thử mở như file XLSX (Excel 2007+)
            try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                StringJoiner joiner = new StringJoiner("\n");

                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    joiner.add("Sheet: " + sheet.getSheetName());

                    for (Row row : sheet) {
                        StringJoiner rowJoiner = new StringJoiner("\t");
                        for (Cell cell : row) {
                            String cellValue = getCellValueAsString(cell);
                            if (!cellValue.trim().isEmpty()) {
                                rowJoiner.add(cellValue);
                            }
                        }

                        String rowText = rowJoiner.toString();
                        if (!rowText.trim().isEmpty()) {
                            joiner.add(rowText);
                        }
                    }
                }
                return joiner.toString();
            }
        } catch (Exception e) {
            log.warn("Error processing Excel file with POI: {}", e.getMessage());

            // Nếu thất bại, thử dùng Tika để trích xuất
            try {
                log.debug("Falling back to Tika for Excel extraction");
                return extractWithTika(inputStream);
            } catch (Exception tikaEx) {
                log.error("Tika extraction also failed: {}", tikaEx.getMessage());
                throw new IOException("Không thể trích xuất văn bản từ file Excel. File có thể bị hỏng hoặc không đúng định dạng Excel.", e);
            }
        }
    }

    // Phương thức riêng để lấy giá trị cell dưới dạng chuỗi
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        // Định dạng số để tránh dấu thập phân không cần thiết
                        double value = cell.getNumericCellValue();
                        if (value == Math.floor(value)) {
                            return String.format("%.0f", value);
                        } else {
                            return String.valueOf(value);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        try {
                            return cell.getStringCellValue();
                        } catch (Exception e2) {
                            return cell.getCellFormula();
                        }
                    }
                case BLANK:
                    return "";
                case ERROR:
                    return "#ERROR#";
                default:
                    return "";
            }
        } catch (Exception e) {
            log.warn("Error getting cell value: {}", e.getMessage());
            return "";
        }
    }

    private String extractWithTika(InputStream inputStream) throws IOException, TikaException, SAXException {
        log.debug("Extracting text with Apache Tika");

        // Trích xuất với Tika
        BodyContentHandler handler = new BodyContentHandler(-1); // -1 means no limit
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        // Tự động phát hiện parser phù hợp
        Parser parser = new PDFParser(); // Default to PDF parser
        parser.parse(inputStream, handler, metadata, context);

        return handler.toString();
    }
}

