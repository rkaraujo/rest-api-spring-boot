package software.renato.timemanager.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import software.renato.timemanager.common.exception.MessageResponseDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthUtil {

    @Autowired
    private ObjectMapper objectMapper;

    public void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(new MessageResponseDTO("Unauthorized")));
    }

}
