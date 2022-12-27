package com.battleship.services;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.models.response.BaseResponse;
import com.battleship.models.response.GameModelUIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class BattleShipServiceRest {
    private static final String URL = "http://localhost:8080/";

    @Autowired
    private RestTemplate restTemplate;

    public GameModelUIResponse getRandomBattleFieldModel(PreparingModel preparingModel, String active) {
        try {
            return restTemplate.postForEntity(
                    URL + active + "/random_battlefield", preparingModel, GameModelUIResponse.class).getBody();
        } catch (Exception e) {
            return GameModelUIResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }

    }

    public BaseResponse deleteGameModel(UUID gameModelId) {
        try {
            return restTemplate.getForEntity(
                    URL + "single_player/game/" + gameModelId + "/delete", BaseResponse.class).getBody();
        } catch (Exception e) {
            return BaseResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }

    public List<FreeGame> getFreeGamesList(UUID playerId) {
        ResponseEntity<List> responseEntity = restTemplate.getForEntity(
                URL + "multiplayer/free_games?withoutId="+playerId, List.class);
        return (List<FreeGame>) responseEntity.getBody();
    }

    public GameModelUIResponse joinToMultiplayerGame(UUID gameId, GameModelUI gameModelUI) {
        try {
            return restTemplate.postForEntity(
                    URL + "multiplayer/game/"+gameId+"/join", gameModelUI, GameModelUIResponse.class).getBody();
        } catch (Exception e) {
            return GameModelUIResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }

    public BaseResponse saveGame(UUID gameModelId) {
        try {
            return restTemplate.getForEntity(
                    URL + "single_player/game/" + gameModelId + "/save", BaseResponse.class).getBody();
        } catch (Exception e) {
            return BaseResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }
}
