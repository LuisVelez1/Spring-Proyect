package Proyect.presentation.controller;

import Proyect.exception.exception.UserNotFoundException;
import Proyect.presentation.dto.AuthLoginRequest;
import Proyect.presentation.dto.AuthResponse;
import Proyect.service.implementation.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@PreAuthorize("permitAll()")
@RequestMapping("/auth")
@Tag(name = "Login", description = "Controller for Authentication")
public class AuthenticationController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Login
    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "The user is login with email and password.",
            tags = {"Login"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login request with email and password",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthLoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login Successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User Not Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid Credentials",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    )
            }
    )
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest userRequest) {
        try {
            AuthResponse authResponse = this.userDetailsService.loginUser(userRequest);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
