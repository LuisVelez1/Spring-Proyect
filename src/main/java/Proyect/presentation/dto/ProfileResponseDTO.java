package Proyect.presentation.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileResponseDTO {
    String email;
    String firstName;
    String lastName;
    String phone;
    String imageURL;
}
