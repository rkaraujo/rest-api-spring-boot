package software.renato.timemanager.time;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "time")
@Data
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Project Id is required")
    private Long projectId;

    @NotNull(message = "User Id is required")
    private Long userId;

    @NotNull(message = "Started at is required")
    private LocalDateTime startedAt;

    @NotNull(message = "Ended at is required")
    private LocalDateTime endedAt;
}
