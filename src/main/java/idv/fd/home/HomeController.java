package idv.fd.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

@RestController
public class HomeController {

    @GetMapping(DEFAULT_PATH_SEPARATOR)
    public Mono<String> index() {

        return Mono.just("up");
    }
}
