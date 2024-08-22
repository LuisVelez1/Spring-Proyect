package Proyect.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private boolean state;
    private String role;
}
