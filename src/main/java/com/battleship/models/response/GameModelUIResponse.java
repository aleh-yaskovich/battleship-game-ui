package com.battleship.models.response;

import com.battleship.models.GameModelUI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GameModelUIResponse extends BaseResponse {
    private GameModelUI gameModelUI;
}
