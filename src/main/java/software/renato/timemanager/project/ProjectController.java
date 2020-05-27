package software.renato.timemanager.project;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;

    private final BoundMapperFacade<ProjectDTO, Project> dtoMapper;

    @Value("${app.max.page.size}")
    private int maxPageSize;

    public ProjectController(ProjectService projectService, MapperFactory mapperFactory) {
        this.projectService = projectService;
        dtoMapper = mapperFactory.getMapperFacade(ProjectDTO.class, Project.class);
    }

    @GetMapping("/v1/project/{id}")
    public ProjectDTO getProject(@PathVariable Long id) {
        Project project = projectService.getProject(id);
        return convertToDto(project);
    }

    @GetMapping("/v1/project")
    public ListProjectDTO getAllProjects(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        if (size > maxPageSize) {
            size = maxPageSize;
        }
        List<Project> allProjects = projectService.getAllProjects(page, size);
        List<ProjectDTO> allProjectsDtos = allProjects.stream().map(this::convertToDto).collect(Collectors.toList());
        return new ListProjectDTO(allProjectsDtos);
    }

    @PostMapping("/v1/project")
    public ProjectDTO insertProject(@RequestBody ProjectDTO projectDTO) {
        Project project = convertToEntity(projectDTO);
        Project savedProject = projectService.insert(project);
        return convertToDto(savedProject);
    }

    @PutMapping("/v1/project/{id}")
    public ProjectDTO updateProject(@RequestBody ProjectDTO projectDTO, @PathVariable Long id) {
        Project project = convertToEntity(projectDTO);
        project.setId(id);
        Project savedProject = projectService.update(project);
        return convertToDto(savedProject);
    }

    private Project convertToEntity(ProjectDTO projectDTO) {
        return dtoMapper.map(projectDTO);
    }

    private ProjectDTO convertToDto(Project project) {
        return dtoMapper.mapReverse(project);
    }
}
