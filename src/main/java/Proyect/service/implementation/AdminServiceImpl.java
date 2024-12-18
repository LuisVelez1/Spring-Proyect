package Proyect.service.implementation;

import Proyect.persistence.entity.AdministratorEntity;
import Proyect.persistence.persistence.AdminRepository;
import Proyect.presentation.dto.ProfileResponseDTO;
import Proyect.presentation.dto.UpdateDTO;
import Proyect.service.interfaces.IAdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {
   private final AdminRepository adminRepository;
   private final ModelMapper modelMapper;

    @Override
    public ProfileResponseDTO profile(String email) {
        AdministratorEntity adminEntity = this.adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        return this.modelMapper.map(adminEntity, ProfileResponseDTO.class);
    }

    @Override
    public UpdateDTO updateAdmin(String email, UpdateDTO updateDTO) {
        AdministratorEntity currentAdmin = this.adminRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " doesn't exist"));

        if (updateDTO.getFirstName() != null) {
            currentAdmin.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            currentAdmin.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhone() != null) {
            currentAdmin.setPhone(updateDTO.getPhone());
        }
        currentAdmin.setState(updateDTO.isState());

        AdministratorEntity updateAdministrator = this.adminRepository.save(currentAdmin);

        return modelMapper.map(updateAdministrator, UpdateDTO.class);
    }

    @Override
    public String deleteAdmin(String email) {
        AdministratorEntity admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin with email " + email + " doesn't exist"));
        adminRepository.delete(admin);
        return "Admin deleted successfully";
    }
}
