package com.udea.fe.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udea.fe.DTO.TaskAssignmentRequestDTO;
import com.udea.fe.DTO.TaskAssignmentResponseDTO;
import com.udea.fe.entity.User;
import com.udea.fe.service.TaskAssignmentService;

@RestController
@RequestMapping("/api/task-assignments")
public class TaskAssignmentController {

    private static final Logger logger = LoggerFactory.getLogger(TaskAssignmentController.class);

    @Autowired
    private TaskAssignmentService taskAssignmentService;

    @PostMapping
    public ResponseEntity<TaskAssignmentResponseDTO> assignTask(@RequestBody TaskAssignmentRequestDTO request) {
        try {
            logger.info("Asignando tarea al usuario con ID: {} y tarea ID: {}", request.getAssignedId(), request.getTaskId());
            TaskAssignmentResponseDTO response = taskAssignmentService.assignTask(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al asignar la tarea: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/task/{taskId}/users")
    public ResponseEntity<List<User>> getUsersAssignedToTask(@PathVariable Long taskId) {
        try {
            logger.info("Obteniendo usuarios asignados a la tarea con ID: {}", taskId);
            List<User> users = taskAssignmentService.getUsersAssignedToTask(taskId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error al obtener usuarios asignados: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
