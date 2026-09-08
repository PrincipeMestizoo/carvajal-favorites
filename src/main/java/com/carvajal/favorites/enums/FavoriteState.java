package com.carvajal.favorites.enums;

/**
 * Estado del item dentro de la lista de deseos.
 * Se recalcula cada vez que se consulta contra el stock real del producto.
 */
public enum FavoriteState {
    DISPONIBLE,
    SIN_STOCK
}
