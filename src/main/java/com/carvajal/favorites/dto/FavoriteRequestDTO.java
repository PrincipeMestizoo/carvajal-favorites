package com.carvajal.favorites.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequestDTO {

    @NotNull(message = "El idProduct es obligatorio")
    private Long idProduct;

    @Min(value = 1, message = "La cantidad debe ser mayor o igual a 1")
    private Integer quantity;
}
