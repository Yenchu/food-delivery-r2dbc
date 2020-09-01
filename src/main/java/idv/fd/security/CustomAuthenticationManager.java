package idv.fd.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@Slf4j
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        if (!User.class.isInstance(authentication.getPrincipal())) {
            log.error("principal is not User instance: {}", authentication.getPrincipal());
            return Mono.empty();
        }

        User user = User.class.cast(authentication.getPrincipal());
        if (user == null) {
            log.error("user is null");
            return Mono.empty();
        }

        String username = user.getUsername();
//        String role = "USER";
//        SimpleGrantedAuthority ga = new SimpleGrantedAuthority(role);
//        List<SimpleGrantedAuthority> authorities = Arrays.asList(ga);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.EMPTY_LIST
        );
        return Mono.just(auth);
    }
}
