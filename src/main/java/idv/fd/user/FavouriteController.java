package idv.fd.user;

import idv.fd.user.dto.AddFavourite;
import idv.fd.user.model.Favourite;
import idv.fd.user.repository.FavouriteRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FavouriteController {

    private FavouriteRepository favouriteRepository;

    public FavouriteController(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @GetMapping("/favourites")
    public Flux<Favourite> findFavourites(
            @RequestParam(name = "userId", required = false) Long userId) {

        if (userId != null) {
            return favouriteRepository.findByUserId(userId);
        } else {
            return favouriteRepository.findAll();
        }
    }

    @PostMapping("/favourites")
    public Mono<Favourite> addFavourite(@RequestBody AddFavourite cf) {

        Favourite fv = Favourite.builder()
                .userId(cf.getUserId()).restaurantId(cf.getRestaurantId()).build();
        return favouriteRepository.save(fv);
    }

    @DeleteMapping("/favourites/{favouriteId}")
    public void removeFavourite(@PathVariable(name = "favouriteId") Long favouriteId) {

        favouriteRepository.deleteById(favouriteId);
    }
}
