package com.carvajal.favorites.model;

import com.carvajal.favorites.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Historico inmutable de cada accion (agregar/actualizar/eliminar)
 * realizada sobre un item de la lista de deseos.
 */
@Entity
@Table(name = "history_favorite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_history")
    private Long idHistory;

    @Column(name = "id_item_favorite", nullable = false)
    private Long idItemFavorite;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private ActionType action;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    @PrePersist
    public void prePersist() {
        if (this.dateAction == null) {
            this.dateAction = LocalDateTime.now();
        }
    }
}
