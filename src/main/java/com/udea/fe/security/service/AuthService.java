package com.udea.fe.security.service;

import com.udea.fe.DTO.AuthResponse;
import com.udea.fe.DTO.LoginRequest;
import com.udea.fe.config.JwtService;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
      )
    );

    User user = userRepository
      .findByEmail(request.getEmail())
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    String token = jwtService.generateToken(user);

    return new AuthResponse(
      token,
      user.getUserId(),
      user.getName(),
      user.getEmail(),
      user.getDni(),
      user.getRole().name(),
      user.getStatus().name()
    );
  }
}
