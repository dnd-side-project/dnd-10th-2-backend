package org.dnd.modutimer.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCheckRequest {

    @NotBlank(message = "email is required.")
    @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Please enter a valid email address")
    @Schema(description = "사용자 이메일", nullable = false, example = "green12@gmail.com")
    private String email;
}
