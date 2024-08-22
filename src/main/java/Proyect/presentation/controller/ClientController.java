package Proyect.presentation.controller;

import Proyect.presentation.dto.ClientDTO;
import Proyect.service.SendEmailService;
import Proyect.service.interfaces.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService clientService;
    private final SendEmailService sendEmailService;

    // Save
    @PreAuthorize("permitAll()")
    @PostMapping("/register")
    public ResponseEntity<ClientDTO> save(@RequestBody ClientDTO clientDTO){
        try {
            ClientDTO savedClient = this.clientService.save(clientDTO);

            String subject = "Welcome to Odyssey Expedition!";
            String body = "Dear " + savedClient.getFirstName() + ",\n\nThanks for registering with us";
            sendEmailService.sendEmail(savedClient.getEmail(), subject, body);

            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
