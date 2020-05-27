package software.renato.timemanager.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDTO {

    private Long id;
    private String title;
    private String description;

    @JsonProperty("user_id")
    private List<Long> userIds;

}
