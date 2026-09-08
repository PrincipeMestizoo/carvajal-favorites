package com.carvajal.favorites.controller;

import com.carvajal.favorites.dto.FavoriteRequestDTO;
import com.carvajal.favorites.dto.FavoriteResponseDTO;
import com.carvajal.favorites.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Gestion de la lista de deseos del usuario autenticado")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "Listar la lista de deseos del usuario autenticado")
    public ResponseEntity<List<FavoriteResponseDTO>> list(Authentication authentication) {
        Long idUser = currentUserId(authentication);
        return ResponseEntity.ok(favoriteService.listByUser(idUser));
    }

    @PostMapping
    @Operation(summary = "Agregar un producto a la lista de deseos")
    public ResponseEntity<FavoriteResponseDTO> add(Authentication authentication,
                                                     @Valid @RequestBody FavoriteRequestDTO request) {
        Long idUser = currentUserId(authentication);
        FavoriteResponseDTO created = favoriteService.add(idUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{idItemFavorite}")
    @Operation(summary = "Actualizar cantidad/producto de un item de la lista de deseos")
    public ResponseEntity<FavoriteResponseDTO> update(Authentication authentication,
                                                        @PathVariable Long idItemFavorite,
                                                        @Valid @RequestBody FavoriteRequestDTO request) {
        Long idUser = currentUserId(authentication);
        return ResponseEntity.ok(favoriteService.update(idUser, idItemFavorite, request));
    }

    @DeleteMapping("/{idItemFavorite}")
    @Operation(summary = "Eliminar un item de la lista de deseos")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long idItemFavorite) {
        Long idUser = currentUserId(authentication);
        favoriteService.delete(idUser, idItemFavorite);
        return ResponseEntity.noContent().build();
    }

    /**
     * El id del usuario viaja en el "subject" del JWT (ver JwtAuthFilter),
     * por eso se obtiene siempre del token y nunca del cliente.
     */
    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
