package Proyect.presentation.controller;

import Proyect.Utils.SecurityUtils;
import Proyect.presentation.dto.*;
import Proyect.service.interfaces.IAdminService;
import Proyect.service.interfaces.IImageService;
import Proyect.service.interfaces.IPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Controller for admin management")
public class AdminController {
   private final IAdminService adminService;
   private final IPasswordService passwordService;
   private final IImageService imageService;

    //PROFILE
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Admin profile",
            description = "The administrator will be able to see your profile",
            tags = {"Profile"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The admin will be able to see your profile through your email ",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponseDTO.class)
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
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Admin not found",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<ProfileResponseDTO> profile() {
        String email = SecurityUtils.getEmail();
        try {
            ProfileResponseDTO profileResponseDTO = this.adminService.profile(email);
            return ResponseEntity.status(HttpStatus.OK).body(profileResponseDTO);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Update information",
            description = "The administrator will be able to update his information",
            tags = {"Update"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The admin will be able to update his/her information through your email ",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponseDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Update information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<UpdateDTO> update(@RequestBody UpdateDTO updateDTO){
        String email = SecurityUtils.getEmail();
        try{
            UpdateDTO updateAdmin = this.adminService.updateAdmin(email, updateDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updateAdmin);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //changePassword
    @PutMapping("/changePassword")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Change Password",
            description = "The administrator will be able to change his password",
            tags = {"Update"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The admin will be able to change his password with his email and his oldPassword ",
                    required = false,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponseDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Change password",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<UpdatePasswordResponseDTO> updatePassword (@RequestBody UpdatePasswordDTO updatePasswordDTO){
        String email = SecurityUtils.getEmail();
        String role = SecurityUtils.getCurrentUserRole();

        updatePasswordDTO.setRole(role);
        updatePasswordDTO.setEmail(email);
        try{
            UpdatePasswordResponseDTO changePassword = this.passwordService.changePassword(updatePasswordDTO);
            return ResponseEntity.status(HttpStatus.OK).body(changePassword);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //Delete
    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Delete Admin",
            description = "Delete an administrator using their email",
            tags = {"Delete"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Admin deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Admin not found"
                    )
            }
    )
    public ResponseEntity<String> delete(@PathVariable String email) {
        try {
            String message = adminService.deleteAdmin(email);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }
    }


    //Save and change Image
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Upload Image from admin",
            description = "The Admin will be upload an image",
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
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<ImageDTO> image (@ModelAttribute ImageDTO imageDTO) {
        String email = SecurityUtils.getEmail();
        try{
            imageDTO.setEmail(email);
            ImageDTO uploadImage = this.imageService.uploadImageAndSaveURL(imageDTO);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
