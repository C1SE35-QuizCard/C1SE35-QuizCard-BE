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
    @Size(max = 500, message = "Max length question is 500.")
    private String question;

    @NotBlank(message = "Answer of the flashcard is empty.")
    @Size(max = 500, message = "Max length answer is 500.")
    private String answer;

    //    private MultipartFile imageData;
    private String imageData;
}
