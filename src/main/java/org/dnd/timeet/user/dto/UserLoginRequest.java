package org.dnd.timeet.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {

    @NotBlank(message = "email is required.")
    @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Please enter a valid email address.")
    @Schema(description = "사용자 이메일", nullable = false, example = "green12@gmail.com")
    private String email;

    @NotBlank(message = "password is required.")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "It must contain letters, numbers, and special characters, and cannot contain spaces.")
    @Schema(description = "사용자 비밀번호", nullable = false, example = "green1234!")
    private String password;
}