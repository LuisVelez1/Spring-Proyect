package Proyect.service.implementation;

import Proyect.Utils.SecurityUtils;
import Proyect.persistence.entity.AdministratorEntity;
import Proyect.persistence.entity.ClientEntity;
import Proyect.persistence.persistence.AdminRepository;
import Proyect.persistence.persistence.ClientRepository;
import Proyect.presentation.dto.ImageDTO;
import Proyect.service.interfaces.IImageService;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {

    private final BlobServiceClient blobServiceClient;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;

    @Value("${azure.container.name}")
    private String containerName;

    @Override
    public ImageDTO uploadImageAndSaveURL(ImageDTO imageDTO) {

        MultipartFile file = imageDTO.getFile();
        String email = imageDTO.getEmail();

        String role = SecurityUtils.getCurrentUserRole();

        String blobName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        var blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobName);

        try (var inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);
            blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }

        String imageUrl = blobClient.getBlobUrl();
        imageDTO.setImageUrl(imageUrl);

        switch (role.toUpperCase()) {
            case "ROLE_CLIENT":
                updateClientImage(email, imageDTO);
                break;
            case "ROLE_ADMINISTRATOR":
                if (isClientEmail(email)) {
                    updateClientImage(email, imageDTO);
                } else {
                    updateAdministratorImage(email, imageDTO);
                }
                break;
            default:
                throw new IllegalArgumentException("Role not recognized: " + role);
        }
        return imageDTO;
    }

    private boolean isClientEmail(String email) {
        return clientRepository.findByEmail(email).isPresent();
    }

    private void updateClientImage(String email, ImageDTO imageDTO) {
        Optional<ClientEntity> clientOptional = clientRepository.findByEmail(email);
        if (clientOptional.isPresent()) {
            ClientEntity client = clientOptional.get();
            String originalRole = client.getRole();
            modelMapper.map(imageDTO, client);
            client.setRole(originalRole);
            clientRepository.save(client);
        } else {
            throw new IllegalArgumentException("Client with email " + email + " not found");
        }
    }

    private void updateAdministratorImage(String email, ImageDTO imageDTO) {
        Optional<AdministratorEntity> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            AdministratorEntity admin = adminOptional.get();
            modelMapper.map(imageDTO, admin);
            adminRepository.save(admin);
        } else {
            throw new IllegalArgumentException("Admin with email " + email + " not found");
        }
    }
}
