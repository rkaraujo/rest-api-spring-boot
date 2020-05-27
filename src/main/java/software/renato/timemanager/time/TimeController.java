package software.renato.timemanager.time;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import software.renato.timemanager.auth.AuthUserDetails;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TimeController {

    private final TimeService timeService;

    private final BoundMapperFacade<TimeDTO, Time> dtoMapper;

    @Value("${app.max.page.size}")
    private int maxPageSize;

    public TimeController(TimeService timeService, MapperFactory mapperFactory) {
        this.timeService = timeService;
        dtoMapper = mapperFactory.getMapperFacade(TimeDTO.class, Time.class);
    }

    @PostMapping("/v1/time")
    public TimeDTO insertTime(@Valid @RequestBody TimeDTO timeDTO, Authentication authentication) {
        Time time = convertToEntity(timeDTO);
        time.setUserId(getUserId(authentication));

        Time savedTime = timeService.insert(time);
        return convertToDto(savedTime);
    }

    @PutMapping("/v1/time/{timeId}")
    public TimeDTO updateTime(@Valid @RequestBody TimeDTO timeDTO, @PathVariable Long timeId, Authentication authentication) {
        Time time = convertToEntity(timeDTO);
        time.setUserId(getUserId(authentication));
        time.setId(timeId);

        Time savedTime = timeService.update(time);
        return convertToDto(savedTime);
    }

    @GetMapping("/v1/time/{projectId}")
    public ListTimeDTO getTimeByProject(@PathVariable Long projectId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        Authentication authentication) {
        if (size > maxPageSize) {
            size = maxPageSize;
        }
        List<Time> times = timeService.getTimesByProjectAndUser(projectId, getUserId(authentication), page, size);
        List<TimeDTO> timeDtos = times.stream().map(this::convertToDto).collect(Collectors.toList());
        return new ListTimeDTO(timeDtos);
    }

    private Long getUserId(Authentication authentication) {
        return ((AuthUserDetails) authentication.getPrincipal()).getUser().getId();
    }

    private Time convertToEntity(TimeDTO timeDTO) {
        return dtoMapper.map(timeDTO);
    }

    private TimeDTO convertToDto(Time time) {
        return dtoMapper.mapReverse(time);
    }

}
