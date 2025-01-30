package ru.test.spring.boot_security.demo.entities;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Username should not be empty")
    @Size(min = 2, max = 100, message = "Incorrect length of the name")
    @Column(name = "name")
    private String name;

    @Column(name = "password_hash")
    @Size(min = 4, max = 100, message = "Incorrect length of the password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}
