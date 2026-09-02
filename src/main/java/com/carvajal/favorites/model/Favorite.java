package com.carvajal.favorites.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Audited;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Audited.Table(name = "favorites")
@Getter
@Setter
@NoArgsConstructor
public class Favorite {

    @jakarta.persistence.Id
    private Long id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_favorite")
    private Long idItemFavorite;

    @Column(name = "id_user", nullable = false)
    private Long idUser;

    @Column(name = "id_product", nullable = false)
    private Long idProduct;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "date_save", nullable = false)
    private LocalDateTime dateSave;

    public Favorite(Long idUser, Long idProduct, Integer quantity, String state, LocalDateTime dateSave) {
        this.idUser = idUser;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.state = state;
        this.dateSave = dateSave;
    }
}
