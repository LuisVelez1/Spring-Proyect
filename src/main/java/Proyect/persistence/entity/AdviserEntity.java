package Proyect.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "adviser")
public class AdviserEntity {
    @Id
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private boolean state;
    private String role;
    private String fkEmail;
}