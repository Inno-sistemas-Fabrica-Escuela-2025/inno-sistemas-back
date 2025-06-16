package com.udea.fe.service;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.DTO.SubmissionRequestDTO;
import com.udea.fe.DTO.SubmissionResponseDTO;
import com.udea.fe.entity.Role;
import com.udea.fe.entity.Submission;
import com.udea.fe.entity.User;
import com.udea.fe.repository.SubmissionRepository;
import com.udea.fe.repository.TaskRepository;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class SubmissionService {

  private final SubmissionRepository submissionRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  public SubmissionResponseDTO createSubmission(SubmissionRequestDTO request) {
    Submission submission = new Submission();
    submission.setContent(request.getContent());
    submission.setFileUrl(request.getFileUrl());
    submission.setSubmittedAt(LocalDateTime.now());
    submission.setTask(
      taskRepository.findById(request.getTaskId()).orElseThrow()
    );
    submission.setUser(
      userRepository.findById(request.getUserId()).orElseThrow()
    );

    Submission saved = submissionRepository.save(submission);

    SubmissionResponseDTO response = new SubmissionResponseDTO();
    response.setSubmissionId(saved.getSubmissionId());
    response.setContent(saved.getContent());
    response.setFileUrl(saved.getFileUrl());
    response.setSubmittedAt(saved.getSubmittedAt());
    response.setTaskId(saved.getTask().getTaskId());
    response.setUserId(saved.getUser().getUserId());

    NotificationDTO notification = new NotificationDTO();
    notification.setUserId(saved.getUser().getUserId());
    notification.setMessage("Se ha realizado una nueva entrega.");
    notification.setType("ENTREGA");

    notificationService.createNotification(notification);

    return response;
  }

  public List<SubmissionResponseDTO> getAllSubmissions() {
    return submissionRepository
      .findAll()
      .stream()
      .map(sub -> {
        SubmissionResponseDTO dto = new SubmissionResponseDTO();
        dto.setSubmissionId(sub.getSubmissionId());
        dto.setContent(sub.getContent());
        dto.setFileUrl(sub.getFileUrl());
        dto.setSubmittedAt(sub.getSubmittedAt());
        dto.setTaskId(sub.getTask().getTaskId());
        dto.setUserId(sub.getUser().getUserId());
        return dto;
      })
      .toList();
  }

  public SubmissionResponseDTO getSubmissionById(Long id) {
    Submission sub = submissionRepository.findById(id).orElseThrow();
    SubmissionResponseDTO dto = new SubmissionResponseDTO();
    dto.setSubmissionId(sub.getSubmissionId());
    dto.setContent(sub.getContent());
    dto.setFileUrl(sub.getFileUrl());
    dto.setSubmittedAt(sub.getSubmittedAt());
    dto.setTaskId(sub.getTask().getTaskId());
    dto.setUserId(sub.getUser().getUserId());
    return dto;
  }

  public List<SubmissionResponseDTO> getSubmissionsByTaskId(Long taskId, String userEmail) {
    User user = userRepository
      .findByEmail(userEmail)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    boolean isTeacher = user.getRole() == Role.TEACHER;

    List<Submission> submissions;
    if (isTeacher) {
      submissions = submissionRepository.findByTask_TaskId(taskId);
    } else {
      submissions = submissionRepository.findByTask_TaskIdAndUser_UserId(
        taskId, user.getUserId()
      );
    }

    return submissions
      .stream()
      .map(sub -> {
        SubmissionResponseDTO dto = new SubmissionResponseDTO();
        dto.setSubmissionId(sub.getSubmissionId());
        dto.setContent(sub.getContent());
        dto.setFileUrl(sub.getFileUrl());
        dto.setSubmittedAt(sub.getSubmittedAt());
        dto.setTaskId(sub.getTask().getTaskId());
        dto.setUserId(sub.getUser().getUserId());
        dto.setUserName(sub.getUser().getName());
        return dto;
      })
      .toList();
  }
}
