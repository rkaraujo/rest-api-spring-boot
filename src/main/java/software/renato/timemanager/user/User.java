package software.renato.timemanager.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_timemanager")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Invalid name length")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email invalid")
    @Size(max = 255, message = "Invalid email length")
    private String email;

    @NotBlank(message = "Login is required")
    @Size(max = 100, message = "Invalid login length")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;

    @Transient
    @Size(min = 6, max = 100, message = "Invalid password length")
    private String rawPassword;

}
