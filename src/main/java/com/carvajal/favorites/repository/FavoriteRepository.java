package com.carvajal.favorites.repository;

import com.carvajal.favorites.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByIdUser(Long idUser);

    Optional<Favorite> findByIdItemFavoriteAndIdUser(Long idItemFavorite, Long idUser);

    Optional<Favorite> findByIdUserAndIdProduct(Long idUser, Long idProduct);

    boolean existsByIdUserAndIdProduct(Long idUser, Long idProduct);
}
