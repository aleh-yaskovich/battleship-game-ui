package com.battleship.services;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
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

    public GameModelUI getRandomBattleFieldModelForSinglePlayer(PreparingModel preparingModel) {
        ResponseEntity<GameModelUI> responseEntity = restTemplate.postForEntity(
                URL + "single_player/random_battlefield", preparingModel, GameModelUI.class);
        return responseEntity.getBody();
    }

    public boolean deleteGameModel(UUID gameModelId) {
        restTemplate.delete(URL + "single_player/game/" + gameModelId, ResponseEntity.class);
        return true;
    }

    public GameModelUI getRandomBattleFieldModelForMultiplayer(PreparingModel preparingModel) {
        ResponseEntity<GameModelUI> responseEntity = restTemplate.postForEntity(
                URL + "multiplayer/random_battlefield", preparingModel, GameModelUI.class);
        return responseEntity.getBody();
    }

    public List<FreeGame> getFreeGamesList(UUID playerId) {
        ResponseEntity<List> responseEntity = restTemplate.getForEntity(
                URL + "multiplayer/free_games?withoutId="+playerId, List.class);
        return (List<FreeGame>) responseEntity.getBody();
    }

    public GameModelUI joinToMultiplayerGame(UUID gameId, GameModelUI gameModelUI) {
        ResponseEntity<GameModelUI> responseEntity = restTemplate.postForEntity(
                URL + "multiplayer/game/"+gameId+"/join", gameModelUI, GameModelUI.class);
        return responseEntity.getBody();
    }
}
