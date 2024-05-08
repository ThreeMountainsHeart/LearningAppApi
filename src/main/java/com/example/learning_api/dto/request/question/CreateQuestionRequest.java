package com.example.learning_api.dto.request.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateQuestionRequest {
    @NotBlank
    private String content;
    @NotBlank
    private String testId;
    private String description;
    @NotBlank
    private String type;
    private MultipartFile source;
    private String createdBy;
    private String updatedBy;
}
