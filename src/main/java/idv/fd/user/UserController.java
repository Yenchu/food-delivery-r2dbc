package idv.fd.user;

import idv.fd.user.api.UserApi;
import idv.fd.user.dto.EditUser;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@Slf4j
public class UserController implements UserApi {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Flux<User> findUsers(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        return userService.findUsers(page, size);
    }

    @PutMapping("/users")
    public Mono<User> updateUser(@Valid @RequestBody EditUser user) {

        log.info("update user: {}", user);
        return userService.updateUser(user);
    }
}
