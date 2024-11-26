package Proyect.presentation.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private boolean state;
    private String role;
}
