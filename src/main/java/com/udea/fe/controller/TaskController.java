package com.udea.fe.controller;

import com.udea.fe.DTO.TaskDTO;
import com.udea.fe.entity.TaskStatus;
import com.udea.fe.service.TaskService;

import java.security.Principal;
import java.util.List;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

  private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

  private final TaskService taskService;

  @PostMapping("/create_task")
  public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
    logger.info("Creando nueva tarea: {}", taskDTO);
    TaskDTO createdTask = taskService.createTask(taskDTO);
    return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
    logger.info("Obteniendo tarea con ID: {}", id);
    TaskDTO taskDTO = taskService.getTaskById(id);
    return ResponseEntity.ok(taskDTO);
  }

  @GetMapping("/all")
  public ResponseEntity<List<TaskDTO>> getAllTasks() {
    logger.info("Obteniendo todas las tareas");
    List<TaskDTO> tasks = taskService.getAllTasks();
    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{id}/edit")
  public ResponseEntity<TaskDTO> updateTask(
    @PathVariable Long id,
    @RequestBody TaskDTO taskDTO
  ) {
    logger.info("Actualizando tarea con ID: {} con datos: {}", id, taskDTO);
    TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
    return ResponseEntity.ok(updatedTask);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<TaskDTO> updateTaskStatus(
    @PathVariable Long id,
    @RequestParam TaskStatus status
  ) {
    logger.info("Actualizando estado de la tarea con ID: {} a: {}", id, status);
    TaskDTO updatedTask = taskService.updateTaskStatus(id, status);
    return ResponseEntity.ok(updatedTask);
  }

  @GetMapping("/project/{projectId}")
  public ResponseEntity<List<TaskDTO>> getTasksByProject(
    @PathVariable Long projectId,
    Principal principal
  ) {
    logger.info("Obteniendo tareas del proyecto con ID: {}", projectId);
    String userEmail = principal.getName();
    List<TaskDTO> tasks = taskService.getTasksByProjectIdAndUser(projectId, userEmail);
    return ResponseEntity.ok(tasks);
  }
}
