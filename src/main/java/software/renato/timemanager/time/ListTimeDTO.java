package software.renato.timemanager.time;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ListTimeDTO {

    @JsonProperty("time")
    private final List<TimeDTO> times;

}
