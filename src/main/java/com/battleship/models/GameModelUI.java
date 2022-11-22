package com.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameModelUI {

    private UUID gameModelId;
    private PlayerModelUI playerModelUI;
    private PlayerModelUI enemyModelUI;
    private boolean botStatus;
}
