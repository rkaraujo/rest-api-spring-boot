package software.renato.timemanager.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.renato.timemanager.common.exception.BusinessException;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User insert(User user) {
        validateExistingLogin(user);
        validateExistingEmail(user);

        user.setPassword(passwordEncoder.encode(user.getRawPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        User dbUser = getUser(user.getId());
        if (dbUser == null) {
            return null;
        }

        if (user.getName() != null) {
            dbUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            boolean isUpdatingEmail = !user.getEmail().equals(dbUser.getEmail());
            if (isUpdatingEmail) {
                validateExistingEmail(user);
            }
            dbUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            boolean isUpdatingLogin = !user.getLogin().equals(dbUser.getLogin());
            if (isUpdatingLogin) {
                validateExistingLogin(user);
            }
            dbUser.setLogin(user.getLogin());
        }
        if (user.getPassword() != null) {
            dbUser.setPassword(passwordEncoder.encode(user.getRawPassword()));
        }
        return userRepository.save(dbUser);
    }

    private void validateExistingEmail(User user) {
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email already exists");
        }
    }

    private void validateExistingLogin(User user) {
        if (user.getLogin() != null && userRepository.existsByLogin(user.getLogin())) {
            throw new BusinessException("Login already exists");
        }
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
