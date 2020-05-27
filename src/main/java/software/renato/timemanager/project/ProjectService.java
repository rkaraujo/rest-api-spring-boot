package software.renato.timemanager.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.renato.timemanager.common.exception.NotFoundException;
import software.renato.timemanager.user.User;
import software.renato.timemanager.user.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Project insert(Project project) {
        List<User> users = getProjectUsers(project.getUserIds());
        project.setUsers(users);
        return projectRepository.save(project);
    }

    @Transactional
    public Project update(Project project) {
        Project dbProject = getProject(project.getId());

        if (project.getTitle() != null) {
            dbProject.setTitle(project.getTitle());
        }
        if (project.getDescription() != null) {
            dbProject.setDescription(project.getDescription());
        }
        if (project.getUserIds() != null) {
            List<User> users = getProjectUsers(project.getUserIds());
            dbProject.setUsers(users);
        }

        return projectRepository.save(dbProject);
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
    }

    public List<Project> getAllProjects(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        Page<Project> pageProject = projectRepository.findAll(pageRequest);
        return pageProject.getContent();
    }

    private List<User> getProjectUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findByIdIn(userIds);
    }

}
