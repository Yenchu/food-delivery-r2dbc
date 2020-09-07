package idv.fd;

import idv.fd.user.UserService;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class UserTests {

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {

        Long id = 1000001L;

        userService.findUserById(id)
                .as(StepVerifier::create)
                .assertNext(u -> assertThat(u).isNull())
                .verifyComplete();

        User user = User.builder()
                .id(id)
                .name("R2 Dbc")
                .build();

        userService.createUser(user)
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .assertNext(u -> assertThat(u.getId()).isEqualTo(id))
                .verifyComplete();
    }
}
