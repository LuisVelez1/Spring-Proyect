package Proyect.presentation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"email", "message", "status", "jwt"})
public record AuthResponse(
    String email,
    String message,
    String jwt,
    Boolean status){}
