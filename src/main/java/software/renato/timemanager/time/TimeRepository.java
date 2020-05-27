package software.renato.timemanager.time;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeRepository extends CrudRepository<Time, Long> {

    // check if userId has a conflicting time
    // overlap = startedAt <= newEndedAt && endedAt >= newStartedAt
    List<Time> findByUserIdAndStartedAtLessThanEqualAndEndedAtGreaterThanEqual(Long userId, LocalDateTime newEndedAt, LocalDateTime newStartedAt);

    Page<Time> findByProjectIdAndUserId(Long projectId, Long userId, Pageable pageable);
}
