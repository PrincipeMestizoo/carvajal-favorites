package com.carvajal.favorites.service;

import com.carvajal.favorites.dto.FavoriteRequestDTO;
import com.carvajal.favorites.dto.FavoriteResponseDTO;
import com.carvajal.favorites.dto.HistoryFavoriteResponseDTO;

import java.util.List;

public interface FavoriteService {

    List<FavoriteResponseDTO> listByUser(Long idUser);

    FavoriteResponseDTO add(Long idUser, FavoriteRequestDTO request);

    FavoriteResponseDTO update(Long idUser, Long idItemFavorite, FavoriteRequestDTO request);

    void delete(Long idUser, Long idItemFavorite);

    List<HistoryFavoriteResponseDTO> getHistory(Long idItemFavorite);
}
