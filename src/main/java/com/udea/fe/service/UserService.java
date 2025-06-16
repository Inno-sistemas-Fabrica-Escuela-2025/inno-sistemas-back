package com.udea.fe.service;

import com.udea.fe.DTO.UserDTO;
import com.udea.fe.entity.Role;
import com.udea.fe.entity.Status;
import com.udea.fe.entity.User;
import com.udea.fe.exception.UserException;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  private static final String MSG_USUARIO_NO_ENCONTRADO = "Usuario no encontrado";

  public UserDTO createUser(UserDTO userDTO) {
    if (userRepository.findByDni(userDTO.getDni()).isPresent()) {
      throw new UserException("Ya existe un usuario con el DNI proporcionado");
    }

    if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
      throw new UserException("Ya existe un usuario con el email proporcionado");
    }

    if (userDTO.getId() != null && userRepository.existsById(userDTO.getId())) {
      throw new UserException("Ya existe un usuario con el ID proporcionado");
    }

    User user = modelMapper.map(userDTO, User.class);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    if (user.getCreatedAt() == null) {
      user.setCreatedAt(LocalDateTime.now());
    }

    if (user.getStatus() == null) {
      user.setStatus(Status.ACTIVE);
    }

    User savedUser = userRepository.save(user);
    return modelMapper.map(savedUser, UserDTO.class);
  }

  public UserDTO getUserByID(Long id) {
    return userRepository
      .findById(id)
      .map(user -> modelMapper.map(user, UserDTO.class))
      .orElseThrow(() -> new UserException(MSG_USUARIO_NO_ENCONTRADO));
  }

  public List<UserDTO> getAllUsers() {
    return userRepository
      .findByRoleNot(Role.ADMIN)
      .stream()
      .map(user -> modelMapper.map(user, UserDTO.class))
      .toList(); // Reemplazo de .collect(Collectors.toList())
  }

  public UserDTO updateUser(Long id, UserDTO userDTO) {
    return userRepository
      .findById(id)
      .map(existingUser -> {
        validarCambioDeDni(userDTO, existingUser);
        validarCambioDeEmail(userDTO, existingUser);
        actualizarCamposUsuario(existingUser, userDTO);
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
      })
      .orElseThrow(() -> new UserException(MSG_USUARIO_NO_ENCONTRADO));
  }

  private void validarCambioDeDni(UserDTO userDTO, User existingUser) {
    if (
      userDTO.getDni() != null &&
      !userDTO.getDni().equals(existingUser.getDni()) &&
      userRepository.findByDni(userDTO.getDni()).isPresent()
    ) {
      throw new UserException("Ya existe otro usuario con el mismo DNI");
    }
  }

  private void validarCambioDeEmail(UserDTO userDTO, User existingUser) {
    if (
      userDTO.getEmail() != null &&
      !userDTO.getEmail().equals(existingUser.getEmail()) &&
      userRepository.findByEmail(userDTO.getEmail()).isPresent()
    ) {
      throw new UserException("Ya existe otro usuario con el mismo email");
    }
  }

  private void actualizarCamposUsuario(User existingUser, UserDTO userDTO) {
    if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
    if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
    if (userDTO.getDni() != null) existingUser.setDni(userDTO.getDni());
    if (userDTO.getRole() != null) existingUser.setRole(userDTO.getRole());
    if (userDTO.getStatus() != null) existingUser.setStatus(userDTO.getStatus());

    if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
      existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }
  }

  public void deleteUser(Long id) {
    userRepository
      .findById(id)
      .orElseThrow(() -> new UserException(MSG_USUARIO_NO_ENCONTRADO));
    userRepository.deleteById(id);
  }

  public void deactivateUser(Long id) {
    User user = userRepository
      .findById(id)
      .orElseThrow(() -> new UserException(MSG_USUARIO_NO_ENCONTRADO));
    user.setStatus(Status.INACTIVE);
    userRepository.save(user);
  }
}
