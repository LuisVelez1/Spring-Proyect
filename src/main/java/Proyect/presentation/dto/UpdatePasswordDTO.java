package Proyect.presentation.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdatePasswordDTO {
    String email;
    String role;
    String oldPassword;
    String newPassword;
}
