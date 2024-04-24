package com.example.learning_api.dto.request.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.example.learning_api.constant.SwaggerConstant.ID_EX;

@Data
public class DeleteCourseRequest {
    @Schema(example = ID_EX)
    @NotBlank
    private String id;
}
