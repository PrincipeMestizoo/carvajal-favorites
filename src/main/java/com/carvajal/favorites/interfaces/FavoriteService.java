package com.carvajal.favorites.interfaces;


import com.carvajal.favorites.dto.FavoriteRequestDto;
import com.carvajal.favorites.dto.FavoriteResponseDto;
import com.carvajal.favorites.model.HistoryFavorite;

import java.util.List;

public interface FavoriteService {
    FavoriteResponseDto addFavorite(FavoriteRequestDto request);
    FavoriteResponseDto updateFavorite(Long idItemFavorite, Integer quantity);
    void deleteFavorite(Long idItemFavorite);
    List<FavoriteResponseDto> getFavoritesByUser(Long idUser);
    List<HistoryFavorite> getHistoryByFavorite(Long idItemFavorite);
}
