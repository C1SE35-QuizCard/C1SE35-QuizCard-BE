package com.example.quizcards.utils;

import com.example.quizcards.entities.questionTypes.QTypes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ClassifyAndDecompressQuestion {
    public Map<String, Object> classifyAndExtract(String input) {
        Map<String, Object> result = new HashMap<>();
        List<String> options = new ArrayList<>();
        StringBuilder currentAnswer = new StringBuilder();
        String question = null;
        boolean foundAnswer = false;

        // Tách dòng và làm sạch khoảng trắng
        String[] lines = input.split("\n");

        // Regex nhận diện đáp án
        Pattern answerPattern = Pattern.compile("^(\\d+[-.)]|[A-Za-z][-.)]).*");

        for (String line : lines) {
            line = line.trim(); // Loại bỏ khoảng trắng thừa
            if (line.isEmpty()) {
                continue; // Bỏ qua dòng trống
            }

            if (question == null) {
                if (!answerPattern.matcher(line).matches()) {
                    question = line; // Gán dòng làm câu hỏi
                    continue;
                } else {
                    result.put("type", QTypes.ESSAY.name()); // Không có câu hỏi, trả về tự luận
                    return result;
                }
            }

            // Nếu dòng khớp với định dạng đáp án
            if (answerPattern.matcher(line).matches()) {
                if (!currentAnswer.isEmpty()) {
                    options.add(currentAnswer.toString().trim()); // Thêm đáp án trước đó vào danh sách
                }
                currentAnswer = new StringBuilder(line); // Bắt đầu đáp án mới
                foundAnswer = true;
            } else if (foundAnswer) {
                // Nếu dòng không khớp định dạng và đã bắt đầu thu thập đáp án
                currentAnswer.append("\n").append(line);
            } else {
                // Nếu không tìm thấy câu hỏi hợp lệ trước đó
                result.put("type", QTypes.ESSAY.name());
                return result;
            }
        }

        // Thêm đáp án cuối cùng (nếu có)
        if (!currentAnswer.isEmpty()) {
            options.add(currentAnswer.toString().trim());
        }

        // Phân loại dựa trên câu hỏi và số lượng đáp án
        if (question != null && options.size() >= 2) {
            result.put("type", QTypes.MULTIPLE.name());
            result.put("question", question);
            result.put("options", options);
        } else {
            result.put("type", QTypes.ESSAY.name());
        }

        return result;
    }
}
