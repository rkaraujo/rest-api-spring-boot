package software.renato.timemanager.user;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.web.bind.annotation.*;
import software.renato.timemanager.common.exception.NotFoundException;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final String MSG_USER_NOT_FOUND = "User not found";

    private final UserService userService;

    private final BoundMapperFacade<UserDTO, User> dtoToEntity;
    private final BoundMapperFacade<User, UserDTO> entityToDto;

    public UserController(UserService userService, MapperFactory mapperFactory) {
        this.userService = userService;
        this.dtoToEntity = mapperFactory.getMapperFacade(UserDTO.class, User.class);
        this.entityToDto = mapperFactory.getMapperFacade(User.class, UserDTO.class);
    }

    @PostMapping("/v1/user")
    public UserDTO insert(@RequestBody UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        user.setId(null);
        
        User savedUser = userService.insert(user);
        return convertToDto(savedUser);
    }

    @PutMapping("/v1/user/{id}")
    public UserDTO update(@RequestBody UserDTO userDTO, @PathVariable Long id) {
        User user = convertToEntity(userDTO);
        user.setId(id);

        User savedUser = userService.update(user);
        if (savedUser == null) {
            throw new NotFoundException(MSG_USER_NOT_FOUND);
        }

        return convertToDto(savedUser);
    }

    @GetMapping("/v1/user/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        if (user == null) {
            throw new NotFoundException(MSG_USER_NOT_FOUND);
        }
        return convertToDto(user);
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = dtoToEntity.map(userDTO);
        user.setRawPassword(userDTO.getPassword());
        return user;
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = entityToDto.map(user);
        userDTO.setPassword(null);
        return userDTO;
    }

}
