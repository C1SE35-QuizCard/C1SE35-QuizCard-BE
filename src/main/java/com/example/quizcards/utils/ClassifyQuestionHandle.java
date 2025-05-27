package com.example.quizcards.utils;

import com.example.quizcards.entities.questionTypes.QTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ClassifyQuestionHandle {
    @Autowired
    private ClassifyAndDecompressQuestion classifyMultipleHandle;

    private static final Pattern ANSWER_PATTERN = Pattern.compile(
            "^(?:\\d+[-.、。)]|[\\p{L}][-.)、。])\\s*",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    /**
     * Xóa phần đáp án ở đầu (nếu có) và dòng trống ở đầu/cuối.
     */
    public static String cleanInput(String input) {
        // Bước 1: Xóa phần đáp án ở đầu (nếu có)
        String cleaned = ANSWER_PATTERN.matcher(input).replaceFirst("");
        // Bước 2: Xóa dòng trống ở đầu
        cleaned = cleaned.replaceAll("^\\s*\\n+", "");
        // Bước 3: Xóa dòng trống ở cuối
        cleaned = cleaned.replaceAll("\\n+\\s*$", "");
        return cleaned;
    }

    public Map<String, Object> getQuestionData(String question, String answer) {
        Map<String, Object> questionData = classifyMultipleHandle.classifyAndExtract(question);
        Map<String, Object> result = new HashMap<>();
        if (questionData.get("type") == null) {
            return null;
        }
        String questionType = QTypes.ESSAY.getType();
        List<String> optionsList = new ArrayList<>();
        long answerIndex = 0L;
        if (!questionData.containsKey("type")) {
            throw new RuntimeException("Missing type");
        }
        if (questionData.get("type").equals(QTypes.MULTIPLE.name())) {
            Object options = questionData.get("options");
            if (!(options instanceof List)) {
                return null;
            }
            optionsList = (List<String>) options;
            for (String option : optionsList) {
//                if (HandleString.removeAllNewLineAndExtraSpace(option).equalsIgnoreCase(
//                        HandleString.removeAllNewLineAndExtraSpace(answer))) {
//                    questionType = QTypes.MULTIPLE.getType();
//                    break;
//                }
                // Code cũ
//                System.out.println("Option: " + option);
//                System.out.println("Answer: " + answer);
//                if (option.equalsIgnoreCase(answer)) {
//                    questionType = QTypes.MULTIPLE.getType();
//                    break;
//                }
//                ++answerIndex;

                // Code mới:
                System.out.println("Option: " + option);
                System.out.println("Answer: " + answer);
                System.out.println("Option cleaned: " + cleanInput(option));
                System.out.println("Answer cleaned: " + cleanInput(answer));
                if (cleanInput(option).equalsIgnoreCase(cleanInput(answer))) {
                    questionType = QTypes.MULTIPLE.getType();
                    break;
                }
                ++answerIndex;
            }
        }
        result.put("type", questionType);
        result.put("question", question);
        result.put("answer", answer);
        if (questionType.equals(QTypes.MULTIPLE.getType())) {
            result.put("options", optionsList);
            result.put("answerIndex", answerIndex);
        }
        return result;
    }
}
