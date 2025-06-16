package com.udea.fe.controller;

import com.udea.fe.DTO.SubmissionRequestDTO;
import com.udea.fe.DTO.SubmissionResponseDTO;
import com.udea.fe.service.SubmissionService;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

  private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

  private final SubmissionService submissionService;

  // Inyección por constructor
  public SubmissionController(SubmissionService submissionService) {
    this.submissionService = submissionService;
  }

  @PostMapping
  public ResponseEntity<SubmissionResponseDTO> createSubmission(@RequestBody SubmissionRequestDTO request) {
    logger.info("Llamada a createSubmission con request: {}", request);
    try {
      SubmissionResponseDTO response = submissionService.createSubmission(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error en createSubmission: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<SubmissionResponseDTO>> getAllSubmissions() {
    logger.info("Llamada a getAllSubmissions");
    try {
      List<SubmissionResponseDTO> response = submissionService.getAllSubmissions();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error en getAllSubmissions: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<SubmissionResponseDTO> getSubmissionById(@PathVariable Long id) {
    logger.info("Llamada a getSubmissionById con id: {}", id);
    try {
      SubmissionResponseDTO response = submissionService.getSubmissionById(id);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error en getSubmissionById: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/by-task/{taskId}")
  public ResponseEntity<List<SubmissionResponseDTO>> getSubmissionsByTask(
    @PathVariable Long taskId,
    Principal principal
  ) {
    logger.info("Llamada a getSubmissionsByTask con taskId: {}", taskId);
    try {
      String userEmail = principal.getName();
      List<SubmissionResponseDTO> response = submissionService.getSubmissionsByTaskId(taskId, userEmail);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error en getSubmissionsByTask: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }
}
