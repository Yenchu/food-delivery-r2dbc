package idv.fd.user;

import idv.fd.user.dto.EditUser;
import idv.fd.user.model.User;
import idv.fd.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<User> findUsers(int page, int size) {

        PageRequest pr = PageRequest.of(page, size, Sort.by("name"));
        return userRepository.findAllByOrderByName(pr);
    }

    public Mono<User> findUserById(Long id) {

        return userRepository.findById(id);
    }

    @Transactional
    public Mono<User> findUserByIdLocked(Long id) {

        return userRepository.findUserByIdLocked(id);
    }

    @Transactional
    public Mono<User> saveUser(User user) {

        return userRepository.save(user);
    }

    @Transactional
    public Mono<User> updateUser(EditUser editUser) {

        return findUserByIdLocked(editUser.getUserId()).flatMap(u -> {
            u.setName(editUser.getUserName());
            return userRepository.save(u);
        });
    }

}
