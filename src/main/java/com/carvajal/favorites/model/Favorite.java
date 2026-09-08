package com.carvajal.favorites.model;

import com.carvajal.favorites.enums.FavoriteState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Item de la lista de deseos de un usuario.
 * Mapea la tabla "favorite" dentro de la base de datos compartida del e-commerce.
 */
@Entity
@Table(name = "favorite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private FavoriteState state;

    @Column(name = "date_save", nullable = false)
    private LocalDateTime dateSave;

    @PrePersist
    public void prePersist() {
        if (this.dateSave == null) {
            this.dateSave = LocalDateTime.now();
        }
        if (this.state == null) {
            this.state = FavoriteState.DISPONIBLE;
        }
    }
}
