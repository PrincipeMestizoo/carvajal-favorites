package com.carvajal.favorites.repository;

import com.carvajal.favorites.model.HistoryFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryFavoriteRepository extends JpaRepository<HistoryFavorite, Long> {

    List<HistoryFavorite> findByIdItemFavoriteOrderByDateActionDesc(Long idItemFavorite);
}
