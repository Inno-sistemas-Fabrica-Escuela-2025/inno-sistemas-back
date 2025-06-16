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
        logger.info("Creando un nuevo equipo: {}", teamDTO.getName());
        return new ResponseEntity<>(teamService.createTeam(teamDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        logger.info("Obteniendo información del equipo con ID: {}", id);
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<List<TeamDTO>> getTeamsByProject(@PathVariable Long projectId) {
        logger.info("Listando todos los equipos del proyecto con ID: {}", projectId);
        return ResponseEntity.ok(teamService.getTeamsByProject(projectId));
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @RequestBody TeamDTO teamDTO
    ) {
        logger.info("Actualizando equipo con ID: {} | Nuevos datos: {}", id, teamDTO.getName());
        return ResponseEntity.ok(teamService.updateTeam(id, teamDTO));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        logger.info("Eliminando equipo con ID: {}", id);
        teamService.deleteTeam(id);
        return ResponseEntity.ok("Equipo eliminado correctamente.");
    }

    @PostMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> addUserToTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "Miembro") String roleInGroup
    ) {
        logger.info("Agregando usuario con ID: {} al equipo con ID: {} como: {}", userId, teamId, roleInGroup);
        teamService.addUserToTeam(userId, teamId, roleInGroup);
        return ResponseEntity.ok("Usuario agregado al equipo correctamente.");
    }

    @GetMapping("/{teamId}/users")
    public ResponseEntity<List<UserTeam>> getUsersByTeam(@PathVariable Long teamId) {
        logger.info("Listando usuarios del equipo con ID: {}", teamId);
        return ResponseEntity.ok(teamService.getUsersByTeam(teamId));
    }

    @DeleteMapping("/{teamId}/users/{userId}")
    public ResponseEntity<String> removeUserFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId
    ) {
        logger.info("Eliminando usuario con ID: {} del equipo con ID: {}", userId, teamId);
        teamService.removeUserFromTeam(userId, teamId);
        return ResponseEntity.ok("Usuario eliminado del equipo correctamente.");
    }
}
