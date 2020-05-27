package software.renato.timemanager.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.renato.timemanager.common.ValidationUtil;
import software.renato.timemanager.common.exception.BusinessException;
import software.renato.timemanager.common.exception.NotFoundException;
import software.renato.timemanager.project.Project;
import software.renato.timemanager.project.ProjectService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeService.class);
    private static final String MSG_TIME_NOT_FOUND = "Time not found";

    private final TimeRepository timeRepository;

    private final ProjectService projectService;

    public TimeService(TimeRepository timeRepository, ProjectService projectService) {
        this.timeRepository = timeRepository;
        this.projectService = projectService;
    }

    @Transactional
    public Time insert(Time time) {
        validateEndAfterStart(time);
        validateTimeOverlap(time);
        validateUserInProject(time.getProjectId(), time.getUserId());

        return timeRepository.save(time);
    }

    @Transactional
    public Time update(Time newTime) {
        Time dbTime = getTime(newTime.getId(), newTime.getUserId());

        validateEndAfterStart(newTime);
        validateTimeOverlap(newTime);
        validateUserInProject(newTime.getProjectId(), newTime.getUserId());

        if (newTime.getProjectId() != null) {
            dbTime.setProjectId(newTime.getProjectId());
        }
        if (newTime.getStartedAt() != null) {
            dbTime.setStartedAt(newTime.getStartedAt());
        }
        if (newTime.getEndedAt() != null) {
            dbTime.setEndedAt(newTime.getEndedAt());
        }

        return timeRepository.save(dbTime);
    }

    public List<Time> getTimesByProjectAndUser(Long projectId, Long userId, int page, int size) {
        validateUserInProject(projectId, userId);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        Page<Time> times = timeRepository.findByProjectIdAndUserId(projectId, userId, pageRequest);
        return times.getContent();
    }

    private void validateTimeBelongsToUser(Time time, Long userId) {
        if (!time.getUserId().equals(userId)) {
            LOGGER.warn("UserId {} tried to access time of another user, time={}", userId, time);
            throw new NotFoundException(MSG_TIME_NOT_FOUND);
        }
    }

    private void validateEndAfterStart(Time time) {
        LocalDateTime start = time.getStartedAt();
        LocalDateTime end = time.getEndedAt();

        // nulls will be validated when saving
        if (ValidationUtil.isAnyNull(start, end)) {
            return;
        }

        if (end.isBefore(start) || end.equals(start)) {
            throw new BusinessException("Start time should be before end time");
        }
    }

    private void validateTimeOverlap(Time time) {
        Long userId = time.getUserId();
        LocalDateTime endedAt = time.getEndedAt();
        LocalDateTime startedAt = time.getStartedAt();

        // nulls will be validated when saving
        if (ValidationUtil.isAnyNull(userId, startedAt, endedAt)) {
            return;
        }

        List<Time> timesOverlaping = timeRepository.findByUserIdAndStartedAtLessThanEqualAndEndedAtGreaterThanEqual(
                userId, endedAt, startedAt);

        boolean isOverlapingWithAnotherTime = timesOverlaping.size() == 1 && !timesOverlaping.get(0).getId().equals(time.getId());
        boolean isOverlapingWithManyTimes = timesOverlaping.size() > 1;
        if (isOverlapingWithAnotherTime || isOverlapingWithManyTimes) {
            String msg = "Time overlaps with another saved times: ";
            String strTimes = timesOverlaping.stream()
                    .map(Time::toString)
                    .collect(Collectors.joining(","));
            throw new BusinessException(msg + strTimes);
        }
    }

    private void validateUserInProject(Long projectId, Long userId) {
        if (ValidationUtil.isAnyNull(projectId, userId)) {
            return;
        }

        Project project = projectService.getProject(projectId);
        if (!project.getUserIds().contains(userId)) {
            LOGGER.warn("UserId {} tried to access project he is not registered, project=", userId, project);
            throw new NotFoundException("User is not in this project");
        }
    }

    private Time getTime(Long id, Long userId) {
        Time time = timeRepository.findById(id).orElseThrow(() -> new NotFoundException(MSG_TIME_NOT_FOUND));
        validateTimeBelongsToUser(time, userId);
        return time;
    }
}
