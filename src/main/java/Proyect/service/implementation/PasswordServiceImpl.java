package Proyect.service.implementation;

import Proyect.persistence.entity.AdministratorEntity;
import Proyect.persistence.entity.ClientEntity;
import Proyect.persistence.persistence.AdminRepository;
import Proyect.persistence.persistence.ClientRepository;
import Proyect.presentation.dto.UpdatePasswordDTO;
import Proyect.presentation.dto.UpdatePasswordResponseDTO;
import Proyect.service.interfaces.IPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements IPasswordService {
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UpdatePasswordResponseDTO changePassword(UpdatePasswordDTO updatePasswordDTO) {
        String email = updatePasswordDTO.getEmail();
        String role = updatePasswordDTO.getRole();
        String oldPassword = updatePasswordDTO.getOldPassword();
        String newPassword = updatePasswordDTO.getNewPassword();

        switch (role) {
            case "ROLE_CLIENT":
                ClientEntity client = clientRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Client Not Found"));
                if(!passwordEncoder.matches(oldPassword, client.getPassword())) {
                    throw new RuntimeException("Current Password is incorrect");
                }

                client.setPassword(passwordEncoder.encode(newPassword));
                clientRepository.save(client);
                break;
            case "ROLE_ADMINISTRATOR":
                AdministratorEntity admin = adminRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Admin not found"));
                if(!passwordEncoder.matches(oldPassword, admin.getPassword())) {
                    throw new RuntimeException("Current Password is incorrect");
                }

                admin.setPassword(passwordEncoder.encode(newPassword));
                adminRepository.save(admin);
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
        return new UpdatePasswordResponseDTO(email, role, "Password update successfully");
    }
}
