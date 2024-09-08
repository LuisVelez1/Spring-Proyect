package Proyect.presentation.controller;

import Proyect.persistence.entity.ClientEntity;
import Proyect.presentation.dto.ClientDTO;
import Proyect.presentation.dto.ImageDTO;
import Proyect.presentation.dto.UpdatePasswordDTO;
import Proyect.presentation.dto.UpdatePasswordResponseDTO;
import Proyect.service.SendEmailService;
import Proyect.service.implementation.ImageServiceImpl;
import Proyect.service.interfaces.IClientService;
import Proyect.service.interfaces.IImageService;
import Proyect.service.interfaces.IPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Controller for Client management")
public class ClientController {

    private final IClientService clientService;
    private final SendEmailService sendEmailService;
    private final IImageService imageService;
    private final IPasswordService passwordService;

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

            String subject = "Welcome to Odyssey Expedition!";
            String body = "Dear " + savedClient.getFirstName() + ",\n\nThanks for registering with us";

            sendEmailService.sendEmail(savedClient.getEmail(), subject, body);

            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    // UploadImage
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('CLIENT')")
    @Operation(
            summary = "Upload Image from Client",
            description = "The client will be upload an image",
            tags = {"Upload Image"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Upload Image request with email, role, file, imageUrl",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Upload Image",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ImageDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<ImageDTO> uploadImage(@ModelAttribute ImageDTO imageDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String role = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse("ROLE_ANONYMOUS");
            String email = (String) authentication.getPrincipal();

            imageDTO.setRole(role);
            imageDTO.setEmail(email);
            ImageDTO uploadImage = this.imageService.uploadImageAndSaveURL(imageDTO);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/updatePassword")
    @PreAuthorize("hasAnyRole('CLIENT')")
    @Operation(
            summary = "Change Password from Client",
            description = "The client will be change his or her password",
            tags = {"Change Password"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Change Password with email, role, oldPassword, newPassword",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdatePasswordDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Change Password",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdatePasswordResponseDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<UpdatePasswordResponseDTO> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .findFirst()
                        .orElse("ROLE_ANONYMUS");
        String email = (String) authentication.getPrincipal();
        updatePasswordDTO.setRole(role);
        updatePasswordDTO.setEmail(email);
        System.out.println("Received UpdatePasswordDTO: " + updatePasswordDTO);

        try {
            UpdatePasswordResponseDTO changePassword = this.passwordService.changePassword(updatePasswordDTO);
            return ResponseEntity.status(HttpStatus.OK).body(changePassword);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
