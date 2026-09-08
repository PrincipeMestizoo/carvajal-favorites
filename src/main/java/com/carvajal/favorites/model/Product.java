package com.carvajal.favorites.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de SOLO LECTURA sobre la tabla "product", que en realidad
 * es propiedad del microservicio de Catalogo/Producto.
 *
 * Como todos los microservicios comparten la misma base de datos,
 * este servicio la usa unicamente para validar existencia y stock
 * al listar/agregar items en la lista de deseos, sin necesidad de
 * hacer una llamada HTTP a otro servicio. NUNCA se escribe en esta
 * tabla desde este microservicio.
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "id_product")
    private Long idProduct;

    @Column(name = "name_product")
    private String nameProduct;

    @Column(name = "price")
    private Long price;

    @Column(name = "stock")
    private Integer stock;
}
