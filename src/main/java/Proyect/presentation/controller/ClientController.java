package Proyect.presentation.controller;

import Proyect.presentation.dto.ClientDTO;
import Proyect.service.SendEmailService;
import Proyect.service.interfaces.IClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Controller for Client management")
public class ClientController {

    private final IClientService clientService;
    private final SendEmailService sendEmailService;

    // Save
    @PreAuthorize("permitAll()")
    @PostMapping("/register")
    @Operation(
            summary = "Register Client",
            description = "The client will register and a confirmation email will be sent back.",
            tags = {"Register"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Register request with email, firstName, lastName, password, phone, state",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Register Successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ClientDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<ClientDTO> save(@RequestBody ClientDTO clientDTO) {
        try {
            ClientDTO savedClient = this.clientService.save(clientDTO);

            // Construir el asunto y cuerpo del correo
            String subject = "Welcome to Odyssey Expedition!";
            String body = "Dear " + savedClient.getFirstName() + ",\n\nThanks for registering with us";

            // Enviar correo electrónico
            sendEmailService.sendEmail(savedClient.getEmail(), subject, body);

            // Devolver la respuesta exitosa
            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {

            // Manejar caso de argumentos inválidos
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
