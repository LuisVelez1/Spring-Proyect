package Proyect.presentation.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ImageDTO {
    private String email;
    private String role;
    private MultipartFile file;
    private String imageUrl;
}
