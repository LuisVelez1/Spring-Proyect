package Proyect.service.interfaces;

import Proyect.presentation.dto.ClientDTO;
import Proyect.presentation.dto.ProfileResponseDTO;
import Proyect.presentation.dto.UpdateDTO;

public interface IClientService {
    ClientDTO save (ClientDTO clientDto);
    ProfileResponseDTO profile (String email);
    UpdateDTO updateClient (String email, UpdateDTO updateDTO);
    String deleteClient(String email, String role, String authenticatedEmail);
}
