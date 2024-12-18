package Proyect.presentation.controller;

import Proyect.Utils.SecurityUtils;
import Proyect.persistence.entity.ClientEntity;
import Proyect.presentation.dto.*;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR')")
    @Operation(
            summary = "Upload Image from Client",
            description = "The client and Admin will be upload an image",
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
    public ResponseEntity<ImageDTO> uploadImage(@ModelAttribute ImageDTO imageDTO, @RequestParam(required = false) String email) {
        try {
            String role = SecurityUtils.getRole();
            String authenticatedEmail = SecurityUtils.getEmail();
            if ("ROLE_ADMINISTRATOR".equals(role) && email != null) {
                imageDTO.setEmail(email);
            } else if ("ROLE_CLIENT".equals(role)) {
                imageDTO.setEmail(authenticatedEmail);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            ImageDTO uploadImage = this.imageService.uploadImageAndSaveURL(imageDTO);
            return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    //Change Password
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

        String role = SecurityUtils.getRole();
        String email = SecurityUtils.getEmail();

        updatePasswordDTO.setRole(role);
        updatePasswordDTO.setEmail(email);
        try {
            UpdatePasswordResponseDTO changePassword = this.passwordService.changePassword(updatePasswordDTO);
            return ResponseEntity.status(HttpStatus.OK).body(changePassword);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //Profile
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR')")
    @Operation(
            summary = "Show profile from client or administrator",
            description = "The client or admin will be see the client profile",
            tags = {"Profile"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "See profile with email if you´re admin or only show your profile if you´re client",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileResponseDTO.class)
                            )
                    )
            }
    )

    public ResponseEntity<ProfileResponseDTO> profile(@RequestParam(required = false) String email) {
        String role = SecurityUtils.getRole();
        String authenticatedEmail = SecurityUtils.getEmail();

        try {
            if ("ROLE_ADMINISTRATOR".equals(role) && email != null) {
                ProfileResponseDTO profileResponse = this.clientService.profile(email);
                return ResponseEntity.status(HttpStatus.OK).body(profileResponse);
            } else if ("ROLE_CLIENT".equals(role)) {
                ProfileResponseDTO profileResponse = this.clientService.profile(authenticatedEmail);
                return ResponseEntity.status(HttpStatus.OK).body(profileResponse);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

     //Update
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR')")
    @Operation(
            summary = "Updating the client data",
            description = "The customer or administrator will be able to update the customer's data.",
            tags = {"Update"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update the Client Data with email if you´re admin or if you´re client you will be edit your information",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Update Profile",
                            content = @Content(
                                    mediaType = "applicatipn/json",
                                    schema = @Schema(implementation = ProfileResponseDTO.class)
                            )
                    )
            }
    )

     public ResponseEntity<UpdateDTO> update (@RequestParam(required = false) String email, @RequestBody UpdateDTO updateDTO) {

        String role = SecurityUtils.getRole();
        String authenticateEmail = SecurityUtils.getEmail();

        try {
            if("ROLE_ADMINISTRATOR".equals(role) && email != null) {
                UpdateDTO updateClient = this.clientService.updateClient(email, updateDTO);
                return ResponseEntity.status(HttpStatus.OK).body(updateClient);
            } else if ("ROLE_CLIENT".equals(role)) {
                UpdateDTO updateClient = this.clientService.updateClient(authenticateEmail, updateDTO);
                return ResponseEntity.status(HttpStatus.OK).body(updateClient);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
     }

     //Delete
    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR')")
    @Operation(
            summary = "Delete Client",
            description = "The client or admin will be delete a client",
            tags = {"Profile"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The customer can delete himself/herself and the administrator will be able delete the client with his email address ",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Delete",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid email",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Client not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )

    public ResponseEntity<String> delete(@PathVariable String email) {
        String role = SecurityUtils.getRole();
        String authenticatedEmail = SecurityUtils.getEmail();

        try {
            String result = clientService.deleteClient(email, role, authenticatedEmail);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado");
        }
    }
}
