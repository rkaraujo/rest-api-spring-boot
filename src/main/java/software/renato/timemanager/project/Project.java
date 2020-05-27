package software.renato.timemanager.project;

import lombok.Data;
import software.renato.timemanager.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @Transient
    private List<Long> userIds;

    public List<Long> getUserIds() {
        if (userIds != null) {
            return userIds;
        }
        if (users == null) {
            return null;
        }
        userIds = users.stream().map(User::getId).collect(Collectors.toList());
        return userIds;
    }

}
