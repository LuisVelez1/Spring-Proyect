package Proyect.service.implementation;

import Proyect.Utils.JwtUtils;
import Proyect.persistence.entity.AdministratorEntity;
import Proyect.persistence.entity.AdviserEntity;
import Proyect.persistence.entity.ClientEntity;
import Proyect.persistence.persistence.AdminRepository;
import Proyect.persistence.persistence.AdviserRepository;
import Proyect.persistence.persistence.ClientRepository;
import Proyect.presentation.dto.AuthLoginRequest;
import Proyect.presentation.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdviserRepository adviserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check Client
        ClientEntity client = clientRepository.findByEmail(username).orElse(null);
        if(client != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(client.getEmail())
                    .password(client.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_CLIENT"))
                    .build();
        }
        // Check Administrator
        AdministratorEntity admin = adminRepository.findByEmail(username).orElse(null);
        if(admin != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    .build();
        }
        // Check Adviser
        AdviserEntity adviser = adviserRepository.findByEmail(username).orElse(null);
        if(adviser != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(adviser.getEmail())
                    .password(adviser.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_ADVISER"))
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username" + username);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest){

        String email = authLoginRequest.email();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(email, "User loged succesfully", accessToken, true);
        return authResponse;
    }

    public Authentication authenticate(String email, String password) {
        UserDetails userDetails = this.loadUserByUsername(email);

        if(userDetails == null) {
            throw new BadCredentialsException(String.format("Invalid email or password"));
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        return new UsernamePasswordAuthenticationToken(email, password, userDetails.getAuthorities());
    }
}
