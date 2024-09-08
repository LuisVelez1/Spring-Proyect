package Proyect.service.implementation;

import Proyect.persistence.entity.AdministratorEntity;
import Proyect.persistence.entity.AdviserEntity;
import Proyect.persistence.entity.ClientEntity;
import Proyect.persistence.persistence.AdminRepository;
import Proyect.persistence.persistence.AdviserRepository;
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
    private final AdviserRepository adviserRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;

    @Value("${azure.container.name}")
    private String containerName;

    @Override
    public ImageDTO uploadImageAndSaveURL(ImageDTO imageDTO) {
        MultipartFile file = imageDTO.getFile();
        String role = imageDTO.getRole();
        String email = imageDTO.getEmail();

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
            case "ROLE_ADVISER":
                updateAdviserImage(email, imageDTO);
                break;
            case "ROLE_ADMINISTRATOR":
                updateAdministratorImage(email, imageDTO);
                break;
            default:
                throw new IllegalArgumentException("Role not recognized: " + role);
        }

        return imageDTO;
    }


    private void updateClientImage(String email, ImageDTO imageDTO) {
        Optional<ClientEntity> clientOptional = clientRepository.findByEmail(email);
        if (clientOptional.isPresent()) {
            ClientEntity client = clientOptional.get();
            modelMapper.map(imageDTO, client);
            clientRepository.save(client);
        } else {
            throw new IllegalArgumentException("Client with email " + email + " not found");
        }
    }

    private void updateAdviserImage(String email, ImageDTO imageDTO) {
        Optional<AdviserEntity> adviserOptional = adviserRepository.findByEmail(email);
        if (adviserOptional.isPresent()) {
            AdviserEntity adviser = adviserOptional.get();
            modelMapper.map(imageDTO, adviser);
            adviserRepository.save(adviser);
        } else {
            throw new IllegalArgumentException("Adviser with email " + email + " not found");
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
