package com.carvajal.favorites.service;

import com.carvajal.favorites.dto.FavoriteRequestDto;
import com.carvajal.favorites.dto.FavoriteResponseDto;
import com.carvajal.favorites.interfaces.FavoriteService;
import com.carvajal.favorites.model.Favorite;
import com.carvajal.favorites.model.HistoryFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final HistoryFavoriteRepository historyRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public FavoriteResponseDto addFavorite(FavoriteRequestDto request) {
        Favorite favorite = favoriteRepository.findByIdUserAndIdProduct(request.getIdUser(), request.getIdProduct())
                .orElse(new Favorite(request.getIdUser(), request.getIdProduct(), request.getQuantity(), "ACTIVE", LocalDateTime.now()));

        favorite.setQuantity(request.getQuantity());
        favorite.setDateSave(LocalDateTime.now());
        Favorite saved = favoriteRepository.save(favorite);

        recordHistory(saved.getIdItemFavorite(), "ADDED_OR_UPDATED");

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public FavoriteResponseDto updateFavorite(Long idItemFavorite, Integer quantity) {
        Favorite favorite = favoriteRepository.findById(idItemFavorite)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en la lista de deseos"));

        favorite.setQuantity(quantity);
        favorite.setDateSave(LocalDateTime.now());
        Favorite updated = favoriteRepository.save(favorite);

        recordHistory(updated.getIdItemFavorite(), "QUANTITY_UPDATED");

        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteFavorite(Long idItemFavorite) {
        Favorite favorite = favoriteRepository.findById(idItemFavorite)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en la lista de deseos"));

        recordHistory(favorite.getIdItemFavorite(), "DELETED");
        favoriteRepository.delete(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponseDto> getFavoritesByUser(Long idUser) {
        return favoriteRepository.findByIdUser(idUser)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryFavorite> getHistoryByFavorite(Long idItemFavorite) {
        return historyRepository.findByIdItemFavorite(idItemFavorite);
    }

    private void recordHistory(Long idItemFavorite, String action) {
        HistoryFavorite history = new HistoryFavorite(idItemFavorite, action, LocalDateTime.now());
        historyRepository.save(history);
    }

    private FavoriteResponseDto mapToDto(Favorite favorite) {
        int currentStock = getProductStock(favorite.getIdProduct());
        boolean outOfStock = currentStock <= 0;
        String message = outOfStock
                ? "¡Advertencia! El producto en su lista de deseos se encuentra actualmente agotado."
                : "Disponible en stock";

        return new FavoriteResponseDto(
                favorite.getIdItemFavorite(),
                favorite.getIdUser(),
                favorite.getIdProduct(),
                favorite.getQuantity(),
                favorite.getState(),
                favorite.getDateSave(),
                outOfStock,
                message
        );
    }

    private int getProductStock(Long idProduct) {
        try {
            Integer stock = jdbcTemplate.queryForObject(
                    "SELECT stock FROM products WHERE id_product = ?",
                    Integer.class,
                    idProduct
            );
            return stock != null ? stock : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
