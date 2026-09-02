package com.mhrs.service;

import com.mhrs.dto.auth.AuthResponse;
import com.mhrs.dto.auth.LoginRequest;
import com.mhrs.dto.auth.RegisterRequest;
import com.mhrs.entity.User;
import com.mhrs.entity.enums.UserRole;
import com.mhrs.repository.UserRepository;
import com.mhrs.security.JwtUtils;
import com.mhrs.security.TcknUtils;
import com.mhrs.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String tcknHash = TcknUtils.hashTckn(request.getTckn());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Bu e-posta adresiyle kayıtlı bir hesap zaten mevcut.");
        }

        if (userRepository.existsByTcknHash(tcknHash)) {
            throw new IllegalArgumentException("Bu T.C. Kimlik Numarası ile kayıtlı bir hesap zaten mevcut.");
        }

        User user = User.builder()
                .tcknHash(tcknHash)
                .maskedTckn(TcknUtils.maskTckn(request.getTckn()))
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber().trim())
                .role(UserRole.ROLE_CITIZEN) // Standart kayıtlar varsayılan CITIZEN açılır
                .build();

        User savedUser = userRepository.save(user);

        // Kayıt sonrası otomatik token üretimi
        String token = jwtUtils.generateToken(savedUser.getId(), savedUser.getEmail(), 900000);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .maskedTckn(savedUser.getMaskedTckn())
                .role(savedUser.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtils.generateAccessToken(authentication);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .fullName(userPrincipal.getFullName())
                .email(userPrincipal.getEmail())
                .maskedTckn(userPrincipal.getMaskedTckn())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                .build();
    }
}