package Proyect.presentation.dto;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ImageDTO {
    private String email;
    @JsonIgnore
    private MultipartFile file;
    private String imageUrl;
}
