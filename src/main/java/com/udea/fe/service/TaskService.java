@Service
@Transactional
@AllArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskAssignmentRepository taskAssignmentRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final UserTeamRepository userTeamRepository;
  private final ModelMapper modelMapper;

  private static final String MSG_USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
  private static final String MSG_TAREA_NO_ENCONTRADA = "Tarea no encontrada";
  private static final String MSG_PROYECTO_NO_ENCONTRADO = "Proyecto no encontrado";

  public TaskDTO createTask(TaskDTO taskDTO) {
    // ...
    Project project = projectRepository
      .findById(taskDTO.getProjectId())
      .orElseThrow(() -> new RuntimeException(MSG_PROYECTO_NO_ENCONTRADO));

    User createdBy = userRepository
      .findById(taskDTO.getCreatedById())
      .orElseThrow(() -> new RuntimeException(MSG_USUARIO_NO_ENCONTRADO));
    // ...
  }

  public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
    return taskRepository
      .findById(id)
      .map(task -> {
        // ...
        if (taskDTO.getProjectId() != null) {
          Project project = projectRepository
            .findById(taskDTO.getProjectId())
            .orElseThrow(() -> new RuntimeException(MSG_PROYECTO_NO_ENCONTRADO));
          task.setProject(project);
        }

        if (taskDTO.getCreatedById() != null) {
          User user = userRepository
            .findById(taskDTO.getCreatedById())
            .orElseThrow(() -> new RuntimeException(MSG_USUARIO_NO_ENCONTRADO));
          task.setCreatedBy(user);
        }
        // ...
      })
      .orElseThrow(() -> new RuntimeException(MSG_TAREA_NO_ENCONTRADA));
  }

  public TaskDTO updateTaskStatus(Long id, TaskStatus status) {
    // ...
    return taskRepository
      .findById(id)
      .map(task -> {
        // ...
      })
      .orElseThrow(() -> new RuntimeException(MSG_TAREA_NO_ENCONTRADA));
  }

  public List<TaskDTO> getTasksByProjectIdAndUser(Long projectId, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
      .orElseThrow(() -> new RuntimeException(MSG_USUARIO_NO_ENCONTRADO));
    // ...
  }
}
