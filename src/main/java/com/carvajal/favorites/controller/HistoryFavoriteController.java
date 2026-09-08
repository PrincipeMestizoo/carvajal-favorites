package com.carvajal.favorites.controller;

import com.carvajal.favorites.dto.HistoryFavoriteResponseDTO;
import com.carvajal.favorites.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites/{idItemFavorite}/history")
@RequiredArgsConstructor
@Tag(name = "History Favorites", description = "Historico de acciones sobre un item de la lista de deseos")
public class HistoryFavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "Consultar el historico de un item de la lista de deseos")
    public ResponseEntity<List<HistoryFavoriteResponseDTO>> getHistory(@PathVariable Long idItemFavorite) {
        return ResponseEntity.ok(favoriteService.getHistory(idItemFavorite));
    }
}
