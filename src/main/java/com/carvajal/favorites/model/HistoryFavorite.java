package com.carvajal.favorites.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class HistoryFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_history")
    private Long idHistory;

    @Column(name = "id_item_favorite", nullable = false)
    private Long idItemFavorite;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    public HistoryFavorite(Long idItemFavorite, String action, LocalDateTime dateAction) {
        this.idItemFavorite = idItemFavorite;
        this.action = action;
        this.dateAction = dateAction;
    }
}
