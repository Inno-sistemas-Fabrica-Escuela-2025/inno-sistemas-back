package com.udea.fe.service;

import com.udea.fe.DTO.TeamDTO;
import com.udea.fe.entity.*;
import com.udea.fe.exception.*;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.TeamRepository;
import com.udea.fe.repository.UserRepository;
import com.udea.fe.repository.UserTeamRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final ModelMapper modelMapper;

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = modelMapper.map(teamDTO, Team.class);

        Project project = projectRepository.findById(teamDTO.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
        team.setProject(project);

        if (teamDTO.getLeaderId() != null) {
            User leader = userRepository.findById(teamDTO.getLeaderId())
                    .orElseThrow(() -> new UserNotFoundException("Usuario líder no encontrado"));
            team.setLeader(leader);
        }

        Team savedTeam = teamRepository.save(team);
        return modelMapper.map(savedTeam, TeamDTO.class);
    }

    public TeamDTO getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .orElseThrow(() -> new TeamNotFoundException("Equipo no encontrado"));
    }

    public List<TeamDTO> getTeamsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));

        return teamRepository.findByProject(project).stream()
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .toList();
    }

    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Equipo no encontrado con ID: " + id));

        existingTeam.setName(teamDTO.getName());
        existingTeam.setDescription(teamDTO.getDescription());

        if (teamDTO.getLeaderId() != null) {
            User leader = userRepository.findById(teamDTO.getLeaderId())
                    .orElseThrow(() -> new UserNotFoundException("Usuario líder no encontrado"));
            existingTeam.setLeader(leader);
        }

        if (teamDTO.getProjectId() != null) {
            Project project = projectRepository.findById(teamDTO.getProjectId())
                    .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
            existingTeam.setProject(project);
        }

        Team updatedTeam = teamRepository.save(existingTeam);
        return modelMapper.map(updatedTeam, TeamDTO.class);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new TeamNotFoundException("Equipo no encontrado con ID: " + id);
        }
        teamRepository.deleteById(id);
    }

    public void addUserToTeam(Long userId, Long teamId, String roleInGroup) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + userId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Equipo no encontrado con ID: " + teamId));

        UserTeamId id = new UserTeamId(userId, teamId);

        if (userTeamRepository.existsById(id)) {
            throw new AlreadyInTeamException("El usuario ya está en el equipo");
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
                .orElseThrow(() -> new TeamNotFoundException("Equipo no encontrado"));
        return userTeamRepository.findByTeam(team);
    }

    public void removeUserFromTeam(Long userId, Long teamId) {
        UserTeamId id = new UserTeamId(userId, teamId);

        if (!userTeamRepository.existsById(id)) {
            throw new NotInTeamException("Usuario no está en el equipo");
        }

        userTeamRepository.deleteById(id);
    }
}