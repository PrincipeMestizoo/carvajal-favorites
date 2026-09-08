package com.carvajal.favorites.exception;

/**
 * Excepcion para reglas de negocio incumplidas (ej: stock insuficiente).
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
