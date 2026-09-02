package com.carvajal.favorites.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequestDto {
    private Long idUser;
    private Long idProduct;
    private Integer quantity;
}
