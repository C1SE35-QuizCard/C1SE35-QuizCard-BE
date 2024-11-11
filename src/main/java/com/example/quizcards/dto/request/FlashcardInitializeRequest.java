package com.example.quizcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardInitializeRequest {
    @NotBlank(message = "Question of the flashcard is empty.")
    @Size(max = 930, message = "Length of question must not be greater than 930")
    private String question;

    @NotBlank(message = "Answer of the flashcard is empty.")
    @Size(max = 1850, message = "Length of answer must not be greater than 1850")
    private String answer;

    //    private MultipartFile imageData;
    private String imageData;
}
