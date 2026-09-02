package com.carvajal.favorites.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponseDto {
    private Long idItemFavorite;
    private Long idUser;
    private Long idProduct;
    private Integer quantity;
    private String state;
    private LocalDateTime dateSave;
    private boolean outOfStockWarning;
    private String message;
}
