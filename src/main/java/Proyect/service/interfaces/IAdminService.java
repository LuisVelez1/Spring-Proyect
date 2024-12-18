package Proyect.service.interfaces;

import Proyect.presentation.dto.ProfileResponseDTO;
import Proyect.presentation.dto.UpdateDTO;

public interface IAdminService {
    ProfileResponseDTO profile (String email);
    UpdateDTO updateAdmin(String email, UpdateDTO updateDTO);
    String deleteAdmin(String email);
}
