package com.udea.fe.service;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.DTO.TaskAssignmentRequestDTO;
import com.udea.fe.DTO.TaskAssignmentResponseDTO;
import com.udea.fe.entity.Task;
import com.udea.fe.entity.TaskAssignment;
import com.udea.fe.entity.TaskAssignmentId;
import com.udea.fe.entity.User;
import com.udea.fe.repository.TaskAssignmentRepository;
import com.udea.fe.repository.TaskRepository;
import com.udea.fe.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskAssignmentService {

  private final TaskRepository taskRepository;
  private final TaskAssignmentRepository taskAssignmentRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  public TaskAssignmentResponseDTO assignTask(TaskAssignmentRequestDTO request) {
    Task task = taskRepository
      .findById(request.getTaskId())
      .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

    TaskAssignment assignment = new TaskAssignment();
    TaskAssignmentId assignmentId = new TaskAssignmentId();
    assignmentId.setTaskId(task.getTaskId());
    assignmentId.setAssignedType(request.getAssignedType());
    assignmentId.setAssignedId(request.getAssignedId());

    assignment.setId(assignmentId);
    assignment.setTask(task);

    taskAssignmentRepository.save(assignment);

    TaskAssignmentResponseDTO response = new TaskAssignmentResponseDTO();
    response.setTaskId(assignmentId.getTaskId());
    response.setAssignedType(assignmentId.getAssignedType());
    response.setAssignedId(assignmentId.getAssignedId());
    response.setMessage("Tarea asignada correctamente");

    NotificationDTO notification = new NotificationDTO();
    notification.setUserId(assignmentId.getAssignedId());
    notification.setMessage("Se le ha asignado una nueva tarea");
    notification.setType("ASIGNMENT");

    notificationService.createNotification(notification);

    return response;
  }

  public List<User> getUsersAssignedToTask(Long taskId) {
    List<TaskAssignment> assignments = taskAssignmentRepository.findById_TaskId(taskId);

    return assignments
      .stream()
      .filter(a -> "USER".equalsIgnoreCase(a.getId().getAssignedType()))
      .map(a -> userRepository.findById(a.getId().getAssignedId()).orElse(null))
      .filter(u -> u != null)
      .collect(Collectors.toList());
  }
}
