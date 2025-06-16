package com.udea.fe.controller;

import com.udea.fe.DTO.AuthResponse;
import com.udea.fe.DTO.LoginRequest;
import com.udea.fe.DTO.UserDTO;
import com.udea.fe.entity.User;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.security.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    try {
      logger.info("Login recibido para usuario: {}", request.getEmail());
      AuthResponse response = authService.login(request);
      logger.info("Login exitoso, generando respuesta");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error en login: {}", e.getMessage(), e);
      return ResponseEntity.status(401).build();
    }
  }

  @GetMapping("/me")
  public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
    logger.info("Obtener usuario autenticado");
    
    if (authentication == null || !authentication.isAuthenticated()) {
      logger.warn("Usuario no autenticado");
      return ResponseEntity.status(401).build();
    }

    Object principal = authentication.getPrincipal();
    logger.debug("Principal: {}", principal);
    logger.debug("Tipo de principal: {}", principal.getClass());

    if (principal instanceof org.springframework.security.core.userdetails.User) {
      String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
      User userEntity = userRepository.findByEmail(username).orElse(null);

      if (userEntity == null) {
        logger.warn("Usuario no encontrado con email: {}", username);
        return ResponseEntity.status(404).build();
      }

      UserDTO userDto = new UserDTO(userEntity);
      logger.info("Usuario autenticado: {}", userDto);
      return ResponseEntity.ok(userDto);
    } else {
      logger.warn("Principal no es instancia válida de UserDetails");
      return ResponseEntity.status(401).build();
    }
  }
}
