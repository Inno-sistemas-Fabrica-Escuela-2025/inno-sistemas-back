package com.udea.fe.service;

import com.udea.fe.DTO.TeamDTO;
import com.udea.fe.entity.*;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.TeamRepository;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.repository.UserTeamRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final ModelMapper modelMapper;

    // 🔸 Constantes para evitar literales duplicados
    private static final String MSG_PROYECTO_NO_ENCONTRADO = "Proyecto no encontrado";
    private static final String MSG_USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String MSG_EQUIPO_NO_ENCONTRADO = "Equipo no encontrado";
    private static final String MSG_EQUIPO_NO_ENCONTRADO_CON_ID = "Equipo no encontrado con ID: ";
    private static final String MSG_USUARIO_NO_ENCONTRADO_CON_ID = "Usuario no encontrado con id: ";
    private static final String MSG_USUARIO_LIDER_NO_ENCONTRADO = "Usuario líder no encontrado";
    private static final String MSG_USUARIO_YA_EN_EQUIPO = "El usuario ya está en el equipo";
    private static final String MSG_USUARIO_NO_ESTA_EN_EQUIPO = "Usuario no está en el equipo";

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = modelMapper.map(teamDTO, Team.class);

        Project project = projectRepository.findById(teamDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException(MSG_PROYECTO_NO_ENCONTRADO));
        team.setProject(project);

        if (teamDTO.getLeaderId() != null) {
            User leader = userRepository.findById(teamDTO.getLeaderId())
                    .orElseThrow(() -> new RuntimeException(MSG_USUARIO_LIDER_NO_ENCONTRADO));
            team.setLeader(leader);
        }

        Team savedTeam = teamRepository.save(team);
        return modelMapper.map(savedTeam, TeamDTO.class);
    }

    public TeamDTO getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .orElseThrow(() -> new RuntimeException(MSG_EQUIPO_NO_ENCONTRADO));
    }

    public List<TeamDTO> getTeamsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException(MSG_PROYECTO_NO_ENCONTRADO));

        return teamRepository.findByProject(project).stream()
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());
    }

    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(MSG_EQUIPO_NO_ENCONTRADO_CON_ID + id));

        existingTeam.setName(teamDTO.getName());
        existingTeam.setDescription(teamDTO.getDescription());

        if (teamDTO.getLeaderId() != null) {
            User leader = userRepository.findById(teamDTO.getLeaderId())
                    .orElseThrow(() -> new RuntimeException(MSG_USUARIO_LIDER_NO_ENCONTRADO));
            existingTeam.setLeader(leader);
        }

        if (teamDTO.getProjectId() != null) {
            Project project = projectRepository.findById(teamDTO.getProjectId())
                    .orElseThrow(() -> new RuntimeException(MSG_PROYECTO_NO_ENCONTRADO));
            existingTeam.setProject(project);
        }

        Team updatedTeam = teamRepository.save(existingTeam);
        return modelMapper.map(updatedTeam, TeamDTO.class);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new RuntimeException(MSG_EQUIPO_NO_ENCONTRADO_CON_ID + id);
        }
        teamRepository.deleteById(id);
    }

    public void addUserToTeam(Long userId, Long teamId, String roleInGroup) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(MSG_USUARIO_NO_ENCONTRADO_CON_ID + userId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException(MSG_EQUIPO_NO_ENCONTRADO_CON_ID + teamId));

        UserTeamId id = new UserTeamId();
        id.setUserId(userId);
        id.setTeamId(teamId);

        if (userTeamRepository.existsById(id)) {
            throw new RuntimeException(MSG_USUARIO_YA_EN_EQUIPO);
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setId(id);
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setRoleInGroup(roleInGroup);

        userTeamRepository.save(userTeam);
    }

    public List<UserTeam> getUsersByTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException(MSG_EQUIPO_NO_ENCONTRADO));
        return userTeamRepository.findByTeam(team);
    }

    public void removeUserFromTeam(Long userId, Long teamId) {
        UserTeamId id = new UserTeamId();
        id.setUserId(userId);
        id.setTeamId(teamId);

        if (!userTeamRepository.existsById(id)) {
            throw new RuntimeException(MSG_USUARIO_NO_ESTA_EN_EQUIPO);
        }

        userTeamRepository.deleteById(id);
    }
}
