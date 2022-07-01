package com.example.paydaystock.service.impl;

import com.example.paydaystock.enums.RoleEnum;
import com.example.paydaystock.exception.EmailAlreadyExistException;
import com.example.paydaystock.exception.EmailOrPasswordInvalid;
import com.example.paydaystock.exception.EnableCheckException;
import com.example.paydaystock.exception.RoleNotFoundException;
import com.example.paydaystock.model.Role;
import com.example.paydaystock.model.User;
import com.example.paydaystock.repository.RoleRepository;
import com.example.paydaystock.repository.UserRepository;
import com.example.paydaystock.resource.LoginRequest;
import com.example.paydaystock.resource.RegisterRequest;
import com.example.paydaystock.resource.TokenResponse;
import com.example.paydaystock.security.JwtUtils;
import com.example.paydaystock.security.userDetails.UserDetailsImpl;
import com.example.paydaystock.service.AuthService;
import com.example.paydaystock.service.MailService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final MailService mailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public User register(RegisterRequest registerRequest, String siteURL) throws MessagingException, UnsupportedEncodingException {

        User user = User.builder()
                .username(registerRequest.getUsername())
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();
        Set<Role> roles = new HashSet<>();

        List<Role> roleList = roleRepository.findAll();
        if (roleList.isEmpty()) {
            roleRepository.save(Role.builder().name(RoleEnum.CUSTOMER).build());
            roleRepository.save(Role.builder().name(RoleEnum.USER).build());
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistException();
        }
        if (registerRequest.getAuthority().contains("CUSTOMER")) {
            Role customer = roleRepository.findByName(RoleEnum.CUSTOMER);
            roles.add(customer);
        }
        else if (registerRequest.getAuthority().contains("USER")) {
            Role userRole = roleRepository.findByName(RoleEnum.USER);
            roles.add(userRole);
        } else {
            throw new RoleNotFoundException();
        }
        user.setRoles(roles);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        userRepository.save(user);
        mailService.sendVerificationEmail(user, siteURL);
        return user;
    }

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        User byEmail = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(EmailOrPasswordInvalid::new);
        boolean matches = passwordEncoder.matches(loginRequest.getPassword(), byEmail.getPassword());
        if (!byEmail.isEnabled()) {
            throw new EnableCheckException();
        }
        if (!matches) {
            throw new EmailOrPasswordInvalid();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new TokenResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                authorities));
    }
}
