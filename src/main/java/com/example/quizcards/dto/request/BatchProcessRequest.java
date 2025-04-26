package com.example.quizcards.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchProcessRequest {
    @NotEmpty(message = "Danh sách ID tệp không được để trống")
    private List<String> fileIds;

    @Min(value = 2, message = "Số lượng flashcard phải lớn hơn 1")
    @Max(value = 200, message = "Số lượng flashcard phải nhỏ hơn 200")
    private Integer numberOfFlashcards = 10;
}
