package com.battleship.services;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.models.response.BaseResponse;
import com.battleship.models.response.GameModelUIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class BattleShipServiceRest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BattleShipServiceRest.class);
    private static final String URL = "http://localhost:8080/";
    @Autowired
    private RestTemplate restTemplate;

    public GameModelUIResponse getRandomBattleFieldModel(PreparingModel preparingModel, String active) {
        LOGGER.debug("getRandomBattleFieldModel("+preparingModel+", "+active+") started");
        try {
            return restTemplate.postForEntity(
                    URL + active + "/random_battlefield", preparingModel, GameModelUIResponse.class).getBody();
        } catch (Exception e) {
            LOGGER.debug("getRandomBattleFieldModel: " + e.getMessage());
            return GameModelUIResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }

    public BaseResponse deleteGameModel(UUID gameModelId) {
        LOGGER.debug("deleteGameModel("+gameModelId+") started");
        try {
            return restTemplate.getForEntity(
                    URL + "single_player/game/" + gameModelId + "/delete", BaseResponse.class).getBody();
        } catch (Exception e) {
            LOGGER.debug("deleteGameModel: " + e.getMessage());
            return BaseResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }

    public List<FreeGame> getFreeGamesList(UUID playerId) {
        LOGGER.debug("getFreeGamesList("+playerId+") started");
        ResponseEntity<List> responseEntity = restTemplate.getForEntity(
                URL + "multiplayer/free_games?withoutId="+playerId, List.class);
        return (List<FreeGame>) responseEntity.getBody();
    }

    public GameModelUIResponse joinToMultiplayerGame(UUID gameId, GameModelUI gameModelUI) {
        LOGGER.debug("joinToMultiplayerGame("+gameId+", "+gameModelUI+") started");
        try {
            return restTemplate.postForEntity(
                    URL + "multiplayer/game/"+gameId+"/join", gameModelUI, GameModelUIResponse.class).getBody();
        } catch (Exception e) {
            LOGGER.debug("joinToMultiplayerGame: " + e.getMessage());
            return GameModelUIResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }

    public BaseResponse saveGame(UUID gameModelId) {
        LOGGER.debug("saveGame("+gameModelId+") started");
        try {
            return restTemplate.getForEntity(
                    URL + "single_player/game/" + gameModelId + "/save", BaseResponse.class).getBody();
        } catch (Exception e) {
            LOGGER.debug("saveGame: " + e.getMessage());
            return BaseResponse.builder()
                    .message("Sorry... Failed to connect to the server. " + e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }
}
