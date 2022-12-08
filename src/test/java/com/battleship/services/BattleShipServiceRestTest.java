package com.battleship.services;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BattleShipServiceRestTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private BattleShipServiceRest serviceRest = new BattleShipServiceRest();

    @Test
    void getRandomBattleFieldModelForSinglePlayerTest() {
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());
        String url = "http://localhost:8080/single_player/random_battlefield";

        when(restTemplate.postForEntity(url, preparingModel, GameModelUI.class))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        GameModelUI actual = serviceRest.getRandomBattleFieldModelForSinglePlayer(preparingModel);
        verify(restTemplate).postForEntity(url, preparingModel, GameModelUI.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void deleteGameModelTest() {
        UUID gameId = UUID.randomUUID();
        String url = "http://localhost:8080/single_player/game/"+gameId;

        serviceRest.deleteGameModel(gameId);
        verify(restTemplate).delete(url, ResponseEntity.class);
    }

    @Test
    void getRandomBattleFieldModelForMultiplayerTest() {
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());
        String url = "http://localhost:8080/multiplayer/random_battlefield";

        when(restTemplate.postForEntity(url, preparingModel, GameModelUI.class))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        GameModelUI actual = serviceRest.getRandomBattleFieldModelForMultiplayer(preparingModel);
        verify(restTemplate).postForEntity(url, preparingModel, GameModelUI.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getFreeGamesListTest() {
        UUID playerId = UUID.randomUUID();
        List<FreeGame> expected = List.of(new FreeGame(UUID.randomUUID(),"Name"));
        String url = "http://localhost:8080/multiplayer/free_games?withoutId="+playerId;

        when(restTemplate.getForEntity(url, List.class)).thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        List<FreeGame> actual = serviceRest.getFreeGamesList(playerId);
        verify(restTemplate).getForEntity(url, List.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void joinToMultiplayerGameTest() {
        UUID gameId = UUID.randomUUID();
        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());
        String url = "http://localhost:8080/multiplayer/game/"+gameId+"/join";

        when(restTemplate.postForEntity(url, new GameModelUI(), GameModelUI.class))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        GameModelUI actual = serviceRest.joinToMultiplayerGame(gameId, new GameModelUI());
        verify(restTemplate).postForEntity(url, new GameModelUI(), GameModelUI.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}