package software.renato.timemanager.auth;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import software.renato.timemanager.user.User;
import software.renato.timemanager.user.UserDTO;
import software.renato.timemanager.user.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final BoundMapperFacade<User, UserDTO> entityToDto;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, MapperFactory mapperFactory) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.entityToDto = mapperFactory.getMapperFacade(User.class, UserDTO.class);
    }

    @PostMapping("/v1/authenticate")
    public AuthDTO authenticate(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getLogin(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();

        return new AuthDTO(jwt, convertToDto(userDetails.getUser()));
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = entityToDto.map(user);
        userDTO.setPassword(null);
        return userDTO;
    }
}
