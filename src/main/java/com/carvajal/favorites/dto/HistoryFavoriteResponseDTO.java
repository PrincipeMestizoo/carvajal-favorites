package com.carvajal.favorites.dto;

import com.carvajal.favorites.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryFavoriteResponseDTO {

    private Long idHistory;
    private Long idItemFavorite;
    private ActionType action;
    private LocalDateTime dateAction;
}
