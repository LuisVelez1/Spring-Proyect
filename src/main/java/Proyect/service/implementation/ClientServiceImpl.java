package Proyect.service.implementation;

import Proyect.persistence.entity.ClientEntity;
import Proyect.persistence.persistence.ClientRepository;
import Proyect.presentation.dto.ClientDTO;
import Proyect.service.interfaces.IClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
}


