package com.udea.fe.controller;

import com.udea.fe.DTO.TeamDTO;
import com.udea.fe.entity.UserTeam;
import com.udea.fe.service.TeamService;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@AllArgsConstructor
public class TeamController {

    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    private final TeamService teamService;

    @PostMapping("create_team")
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        logger.info("Solicitud para crear un nuevo equipo recibida");
        return new ResponseEntity<>(teamService.createTeam(teamDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        logger.info("Solicitud para obtener información de un equipo por ID");
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<List<TeamDTO>> getTeamsByProject(@PathVariable Long projectId) {
        logger.info("Solicitud para listar equipos de un proyecto");
        return ResponseEntity.ok(teamService.getTeamsByProject(projectId));
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @RequestBody TeamDTO teamDTO
    ) {
        logger.info("Solicitud para actualizar un equipo");
        return ResponseEntity.ok(teamService.updateTeam(id, teamDTO));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        logger.info("Solicitud para eliminar un equipo");
        teamService.deleteTeam(id);
        return ResponseEntity.ok("Equipo eliminado correctamente.");
    }

    @PostMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> addUserToTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "Miembro") String roleInGroup
    ) {
        logger.info("Solicitud para agregar usuario a un equipo");
        teamService.addUserToTeam(userId, teamId, roleInGroup);
        return ResponseEntity.ok("Usuario agregado al equipo correctamente.");
    }

    @GetMapping("/{teamId}/users")
    public ResponseEntity<List<UserTeam>> getUsersByTeam(@PathVariable Long teamId) {
        logger.info("Solicitud para obtener usuarios de un equipo");
        return ResponseEntity.ok(teamService.getUsersByTeam(teamId));
    }

    @DeleteMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> removeUserFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId
    ) {
        logger.info("Solicitud para eliminar usuario de un equipo");
        teamService.removeUserFromTeam(userId, teamId);
        return ResponseEntity.ok("Usuario eliminado del equipo correctamente.");
    }
}
