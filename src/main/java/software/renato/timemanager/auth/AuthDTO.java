package software.renato.timemanager.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import software.renato.timemanager.user.UserDTO;

@Data
public class AuthDTO {

    private final String token;

    @JsonProperty("user")
    private final UserDTO userDTO;

}
