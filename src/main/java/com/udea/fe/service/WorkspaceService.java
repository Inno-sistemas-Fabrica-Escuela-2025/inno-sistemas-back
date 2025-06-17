package com.udea.fe.service;

import com.udea.fe.DTO.WorkspaceDTO;
import com.udea.fe.entity.Project;
import com.udea.fe.entity.Workspace;
import com.udea.fe.exception.ProjectNotFoundException;
import com.udea.fe.exception.WorkspaceNotFoundException;
import com.udea.fe.repository.ProjectRepository;
import com.udea.fe.repository.WorkspaceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public WorkspaceDTO createWorkspace(WorkspaceDTO workspaceDTO) {
        Workspace workspace = modelMapper.map(workspaceDTO, Workspace.class);

        Project project = projectRepository.findById(workspaceDTO.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(
                        "Proyecto no encontrado con id: " + workspaceDTO.getProjectId()));
        workspace.setProject(project);

        Workspace savedWorkspace = workspaceRepository.save(workspace);
        return modelMapper.map(savedWorkspace, WorkspaceDTO.class);
    }

    public WorkspaceDTO getWorkspaceById(Long id) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException(
                        "Workspace no encontrado con id: " + id));
        return modelMapper.map(workspace, WorkspaceDTO.class);
    }

    public List<WorkspaceDTO> getAllWorkspaces() {
        return workspaceRepository.findAll().stream()
                .map(workspace -> modelMapper.map(workspace, WorkspaceDTO.class))
                .toList(); // modern approach
    }

    public WorkspaceDTO updateWorkspace(Long id, WorkspaceDTO workspaceDTO) {
        return workspaceRepository.findById(id)
                .map(workspace -> {
                    modelMapper.map(workspaceDTO, workspace);

                    if (workspaceDTO.getProjectId() != null) {
                        Project project = projectRepository.findById(workspaceDTO.getProjectId())
                                .orElseThrow(() -> new ProjectNotFoundException(
                                        "Proyecto no encontrado con id: " + workspaceDTO.getProjectId()));
                        workspace.setProject(project);
                    }

                    Workspace updatedWorkspace = workspaceRepository.save(workspace);
                    return modelMapper.map(updatedWorkspace, WorkspaceDTO.class);
                })
                .orElseThrow(() -> new WorkspaceNotFoundException(
                        "Workspace no encontrado con id: " + id));
    }

    public void deleteWorkspace(Long id) {
        if (!workspaceRepository.existsById(id)) {
            throw new WorkspaceNotFoundException("Workspace no encontrado con id: " + id);
        }
        workspaceRepository.deleteById(id);
    }
}
