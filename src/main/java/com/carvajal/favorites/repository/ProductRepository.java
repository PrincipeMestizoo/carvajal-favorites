package com.carvajal.favorites.repository;

import com.carvajal.favorites.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de SOLO LECTURA sobre la tabla "product" (propiedad del
 * microservicio de Catalogo). No se deben usar metodos de escritura
 * (save/delete) desde este microservicio.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdProduct(Long idProduct);
}
