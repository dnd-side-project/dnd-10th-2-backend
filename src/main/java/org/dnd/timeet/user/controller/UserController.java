package org.dnd.timeet.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.common.exception.InternalServerError;
import org.dnd.timeet.common.security.JWTProvider;
import org.dnd.timeet.user.application.UserService;
import org.dnd.timeet.user.dto.EmailCheckRequest;
import org.dnd.timeet.user.dto.UserLoginRequest;
import org.dnd.timeet.user.dto.UserLoginResponse;
import org.dnd.timeet.user.dto.UserRegisterRequest;
import org.dnd.timeet.common.utils.ApiUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User 컨트롤러", description = "User API입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/check")
    @Operation(summary = "이메일 중복 검사", description = "입력받은 이메일 주소가 이미 사용 중인지 확인한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이메일 사용 가능"),
        @ApiResponse(responseCode = "400", description = "중복된 이메일 존재 또는 잘못된 요청", content = @Content(schema = @Schema(implementation = BadRequestError.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = InternalServerError.class)))
    })
    public ResponseEntity<?> checkEmail(@RequestBody @Valid EmailCheckRequest emailCheckDTO) {
        userService.checkSameEmail(emailCheckDTO.getEmail());

        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "유저 등록 성공"),
        @ApiResponse(responseCode = "400", description = "중복된 이메일 존재 또는 잘못된 요청", content = @Content(schema = @Schema(implementation = BadRequestError.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = InternalServerError.class)))
    })
    @Operation(summary = "유저 등록", description = "새롭게 유저를 등록한다.")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest requestDTO) {
        userService.register(requestDTO);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @PostMapping("/login")
    @Operation(summary = "유저 로그인", description = "사용자의 이메일과 비밀번호를 받아 로그인을 처리한다.")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginRequest requestDTO) {
        UserLoginResponse response = userService.login(requestDTO);

        return ResponseEntity.ok().header(JWTProvider.HEADER, response.getJwtToken()).body(ApiUtils.success(null));
    }

}

