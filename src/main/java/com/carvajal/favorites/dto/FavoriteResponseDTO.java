package com.carvajal.favorites.dto;

import com.carvajal.favorites.enums.FavoriteState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de salida de un item de la lista de deseos, enriquecido
 * con datos actuales del producto (nombre, precio, stock).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponseDTO {

    private Long idItemFavorite;
    private Long idUser;
    private Long idProduct;
    private String nameProduct;
    private Long price;
    private Integer stockAvailable;
    private Integer quantity;
    private FavoriteState state;
    private LocalDateTime dateSave;
    private boolean outOfStock;
}
