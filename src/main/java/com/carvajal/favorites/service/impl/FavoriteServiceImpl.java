package com.carvajal.favorites.service.impl;

import com.carvajal.favorites.dto.FavoriteRequestDTO;
import com.carvajal.favorites.dto.FavoriteResponseDTO;
import com.carvajal.favorites.dto.HistoryFavoriteResponseDTO;
import com.carvajal.favorites.enums.ActionType;
import com.carvajal.favorites.enums.FavoriteState;
import com.carvajal.favorites.exception.BusinessException;
import com.carvajal.favorites.exception.ResourceNotFoundException;
import com.carvajal.favorites.model.Favorite;
import com.carvajal.favorites.model.HistoryFavorite;
import com.carvajal.favorites.model.Product;
import com.carvajal.favorites.repository.FavoriteRepository;
import com.carvajal.favorites.repository.HistoryFavoriteRepository;
import com.carvajal.favorites.repository.ProductRepository;
import com.carvajal.favorites.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final HistoryFavoriteRepository historyFavoriteRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponseDTO> listByUser(Long idUser) {
        return favoriteRepository.findByIdUser(idUser)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public FavoriteResponseDTO add(Long idUser, FavoriteRequestDTO request) {
        Product product = getProductOrThrow(request.getIdProduct());

        if (favoriteRepository.existsByIdUserAndIdProduct(idUser, request.getIdProduct())) {
            throw new BusinessException("El producto ya se encuentra en la lista de deseos del usuario");
        }

        FavoriteState state = resolveState(product, request.getQuantity());

        Favorite favorite = Favorite.builder()
                .idUser(idUser)
                .idProduct(request.getIdProduct())
                .quantity(request.getQuantity())
                .state(state)
                .dateSave(LocalDateTime.now())
                .build();

        favorite = favoriteRepository.save(favorite);
        registerHistory(favorite.getIdItemFavorite(), ActionType.AGREGADO);

        return toResponseDTO(favorite, product);
    }

    @Override
    public FavoriteResponseDTO update(Long idUser, Long idItemFavorite, FavoriteRequestDTO request) {
        Favorite favorite = favoriteRepository.findByIdItemFavoriteAndIdUser(idItemFavorite, idUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el item " + idItemFavorite + " en la lista de deseos del usuario " + idUser));

        Product product = getProductOrThrow(request.getIdProduct());

        favorite.setIdProduct(request.getIdProduct());
        favorite.setQuantity(request.getQuantity());
        favorite.setState(resolveState(product, request.getQuantity()));

        favorite = favoriteRepository.save(favorite);
        registerHistory(favorite.getIdItemFavorite(), ActionType.ACTUALIZADO);

        return toResponseDTO(favorite, product);
    }

    @Override
    public void delete(Long idUser, Long idItemFavorite) {
        Favorite favorite = favoriteRepository.findByIdItemFavoriteAndIdUser(idItemFavorite, idUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el item " + idItemFavorite + " en la lista de deseos del usuario " + idUser));

        favoriteRepository.delete(favorite);
        registerHistory(idItemFavorite, ActionType.ELIMINADO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryFavoriteResponseDTO> getHistory(Long idItemFavorite) {
        return historyFavoriteRepository.findByIdItemFavoriteOrderByDateActionDesc(idItemFavorite)
                .stream()
                .map(h -> HistoryFavoriteResponseDTO.builder()
                        .idHistory(h.getIdHistory())
                        .idItemFavorite(h.getIdItemFavorite())
                        .action(h.getAction())
                        .dateAction(h.getDateAction())
                        .build())
                .toList();
    }

    // ---- helpers ----

    private Product getProductOrThrow(Long idProduct) {
        return productRepository.findByIdProduct(idProduct)
                .orElseThrow(() -> new ResourceNotFoundException("El producto " + idProduct + " no existe en el catalogo"));
    }

    private FavoriteState resolveState(Product product, Integer requestedQuantity) {
        if (product.getStock() == null || product.getStock() <= 0 || product.getStock() < requestedQuantity) {
            return FavoriteState.SIN_STOCK;
        }
        return FavoriteState.DISPONIBLE;
    }

    private void registerHistory(Long idItemFavorite, ActionType action) {
        HistoryFavorite history = HistoryFavorite.builder()
                .idItemFavorite(idItemFavorite)
                .action(action)
                .dateAction(LocalDateTime.now())
                .build();
        historyFavoriteRepository.save(history);
    }

    private FavoriteResponseDTO toResponseDTO(Favorite favorite) {
        Product product = productRepository.findByIdProduct(favorite.getIdProduct()).orElse(null);
        return toResponseDTO(favorite, product);
    }

    private FavoriteResponseDTO toResponseDTO(Favorite favorite, Product product) {
        boolean outOfStock = product == null
                || product.getStock() == null
                || product.getStock() <= 0
                || product.getStock() < favorite.getQuantity();

        return FavoriteResponseDTO.builder()
                .idItemFavorite(favorite.getIdItemFavorite())
                .idUser(favorite.getIdUser())
                .idProduct(favorite.getIdProduct())
                .nameProduct(product != null ? product.getNameProduct() : "Producto no disponible")
                .price(product != null ? product.getPrice() : null)
                .stockAvailable(product != null ? product.getStock() : 0)
                .quantity(favorite.getQuantity())
                .state(outOfStock ? FavoriteState.SIN_STOCK : FavoriteState.DISPONIBLE)
                .dateSave(favorite.getDateSave())
                .outOfStock(outOfStock)
                .build();
    }
}
