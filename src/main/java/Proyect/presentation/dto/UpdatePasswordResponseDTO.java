package Proyect.presentation.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdatePasswordResponseDTO {
    String email;
    String role;
    String message;
}
