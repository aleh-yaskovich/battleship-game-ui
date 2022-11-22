package com.battleship.services;

import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BattleShipServiceRest {
    private static final String URL = "http://localhost:8080/";

    @Autowired
    private RestTemplate restTemplate;

    public GameModelUI getRandomBattleFieldModel(PreparingModel preparingModel) {
        ResponseEntity<GameModelUI> responseEntity = restTemplate.postForEntity(
                URL + "single_player/preparing/random_battlefield", preparingModel, GameModelUI.class);
        return responseEntity.getBody();
    }
}
