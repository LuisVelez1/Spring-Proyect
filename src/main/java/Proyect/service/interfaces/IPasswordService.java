package Proyect.service.interfaces;

import Proyect.presentation.dto.UpdatePasswordDTO;
import Proyect.presentation.dto.UpdatePasswordResponseDTO;

public interface IPasswordService {
    UpdatePasswordResponseDTO changePassword (UpdatePasswordDTO updatePasswordDTO);
}
