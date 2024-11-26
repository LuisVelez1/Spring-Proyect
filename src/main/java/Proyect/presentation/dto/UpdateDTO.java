package Proyect.presentation.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDTO {
    String firstName;
    String lastName;
    String phone;
    boolean state;
}
