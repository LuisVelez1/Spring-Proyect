package Proyect.service.implementation;

import Proyect.persistence.entity.ClientEntity;
import Proyect.persistence.persistence.ClientRepository;
import Proyect.presentation.dto.ClientDTO;
import Proyect.presentation.dto.ProfileResponseDTO;
import Proyect.presentation.dto.UpdateDTO;
import Proyect.service.interfaces.IClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ClientDTO save(ClientDTO clientDto) {

        if (clientDto.getRole() == null || clientDto.getRole().isEmpty()) {
            clientDto.setRole("CLIENT");
        }

        clientDto.setPassword(passwordEncoder.encode(clientDto.getPassword()));

        ClientEntity userEntity = this.modelMapper.map(clientDto, ClientEntity.class);
        ClientEntity userSaved = this.clientRepository.save(userEntity);

        return this.modelMapper.map(userSaved, ClientDTO.class);
    }

    @Override
    public ProfileResponseDTO profile(String email) {
        ClientEntity clientEntity = this.clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        return this.modelMapper.map(clientEntity, ProfileResponseDTO.class);
    }

    @Override
    public UpdateDTO updateClient(String email, UpdateDTO updateDTO) {
        ClientEntity currentClient = this.clientRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " doesn't exist"));

        if (updateDTO.getFirstName() != null) {
            currentClient.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            currentClient.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhone() != null) {
            currentClient.setPhone(updateDTO.getPhone());
        }
        currentClient.setState(updateDTO.isState());

        ClientEntity clientUpdated = this.clientRepository.save(currentClient);

        return this.modelMapper.map(clientUpdated, UpdateDTO.class);
    }

    @Override
    public String deleteClient(String email, String role, String authenticatedEmail) {
        if ("ROLE_ADMINISTRATOR".equals(role)) {
            return deleteByEmail(email);
        } else if ("ROLE_CLIENT".equals(role)) {
            if (!authenticatedEmail.equals(email)) {
                throw new SecurityException("You don't delete other client :(");
            }
            return deleteByEmail(authenticatedEmail);
        } else {
            throw new SecurityException("Rol unauthorized");
        }
    }

    private String deleteByEmail(String email) {
        ClientEntity currentClient = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " doesn't exist"));

        clientRepository.delete(currentClient);
        return "User deleted successfully";
    }

}


