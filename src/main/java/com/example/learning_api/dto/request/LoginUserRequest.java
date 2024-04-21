package com.example.learning_api.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import static com.example.learning_api.constant.SwaggerConstant.*;

@Data
public class LoginUserRequest {
    @Schema(example = EMAIL_EX)
    @Email
    @NotBlank
    private String email;

    @Schema(example = PASSWORD_EX)
    @NotBlank
    @Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    private String password;

}
