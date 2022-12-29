package com.battleship.services;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.models.response.BaseResponse;
import com.battleship.models.response.GameModelUIResponse;
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
        String active = "single_player";
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        GameModelUIResponse expected = GameModelUIResponse.builder()
                .gameModelUI(gameModelUI)
                .status(BaseResponse.Status.SUCCESS)
                .build();
        ResponseEntity<GameModelUIResponse> response = new ResponseEntity<>(expected, HttpStatus.OK);
        String url = "http://localhost:8080/single_player/random_battlefield";

        when(restTemplate.postForEntity(url, preparingModel, GameModelUIResponse.class)).thenReturn(response);

        GameModelUIResponse actual = serviceRest.getRandomBattleFieldModel(preparingModel, active);
        verify(restTemplate).postForEntity(url, preparingModel, GameModelUIResponse.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getRandomBattleFieldModelForMultiplayerTest() {
        String active = "multiplayer";
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        GameModelUIResponse expected = GameModelUIResponse.builder()
                .gameModelUI(gameModelUI)
                .status(BaseResponse.Status.SUCCESS)
                .build();
        ResponseEntity<GameModelUIResponse> response = new ResponseEntity<>(expected, HttpStatus.OK);
        String url = "http://localhost:8080/multiplayer/random_battlefield";

        when(restTemplate.postForEntity(url, preparingModel, GameModelUIResponse.class)).thenReturn(response);

        GameModelUIResponse actual = serviceRest.getRandomBattleFieldModel(preparingModel, active);
        verify(restTemplate).postForEntity(url, preparingModel, GameModelUIResponse.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getRandomBattleFieldModelWithExceptionTest() {
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        String active = "single_player";

        String exceptionMessage = "TEST";
        String url = "http://localhost:8080/single_player/random_battlefield";

        when(restTemplate.postForEntity(url, preparingModel, GameModelUIResponse.class))
                .thenThrow(new RuntimeException(exceptionMessage));

        GameModelUIResponse actual = serviceRest.getRandomBattleFieldModel(preparingModel, active);
        assertNotNull(actual);
        assertNull(actual.getGameModelUI());
        assertEquals(BaseResponse.Status.FAILURE, actual.getStatus());
        assertTrue(actual.getMessage().contains(exceptionMessage));
    }

    @Test
    void deleteGameModelTest() {
        UUID gameId = UUID.randomUUID();
        String url = "http://localhost:8080/single_player/game/" + gameId + "/delete";
        BaseResponse expected = BaseResponse.builder().status(BaseResponse.Status.SUCCESS).build();
        ResponseEntity<BaseResponse> response = new ResponseEntity<>(expected, HttpStatus.OK);

        when(restTemplate.getForEntity(url, BaseResponse.class)).thenReturn(response);

        BaseResponse actual = serviceRest.deleteGameModel(gameId);
        verify(restTemplate).getForEntity(url, BaseResponse.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void deleteGameModelWithExceptionTest() {
        UUID gameId = UUID.randomUUID();
        String url = "http://localhost:8080/single_player/game/" + gameId + "/delete";
        String exceptionMessage = "TEST";

        when(restTemplate.getForEntity(url, BaseResponse.class)).thenThrow(new RuntimeException(exceptionMessage));

        BaseResponse actual = serviceRest.deleteGameModel(gameId);
        verify(restTemplate).getForEntity(url, BaseResponse.class);
        assertNotNull(actual);
        assertEquals(BaseResponse.Status.FAILURE, actual.getStatus());
        assertTrue(actual.getMessage().contains(exceptionMessage));
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
        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        GameModelUIResponse expected = GameModelUIResponse.builder()
                .gameModelUI(gameModelUI)
                .status(BaseResponse.Status.SUCCESS)
                .build();
        ResponseEntity<GameModelUIResponse> response = new ResponseEntity<>(expected, HttpStatus.OK);
        String url = "http://localhost:8080/multiplayer/game/"+gameId+"/join";

        when(restTemplate.postForEntity(url, gameModelUI, GameModelUIResponse.class))
                .thenReturn(response);

        GameModelUIResponse actual = serviceRest.joinToMultiplayerGame(gameId, gameModelUI);
        verify(restTemplate).postForEntity(url, gameModelUI, GameModelUIResponse.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void joinToMultiplayerGameWithExceptionTest() {
        UUID gameId = UUID.randomUUID();
        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        String url = "http://localhost:8080/multiplayer/game/"+gameId+"/join";
        String exceptionMessage = "TEST";

        when(restTemplate.postForEntity(url, gameModelUI, GameModelUIResponse.class))
                .thenThrow(new RuntimeException(exceptionMessage));

        GameModelUIResponse actual = serviceRest.joinToMultiplayerGame(gameId, gameModelUI);
        verify(restTemplate).postForEntity(url, gameModelUI, GameModelUIResponse.class);
        assertNotNull(actual);
        assertNull(actual.getGameModelUI());
        assertEquals(BaseResponse.Status.FAILURE, actual.getStatus());
        assertTrue(actual.getMessage().contains(exceptionMessage));
    }

    @Test
    void saveGameTest() {
        UUID gameId = UUID.randomUUID();
        String url = "http://localhost:8080/single_player/game/" + gameId + "/save";
        BaseResponse expected = BaseResponse.builder().status(BaseResponse.Status.SUCCESS).build();
        ResponseEntity<BaseResponse> response = new ResponseEntity<>(expected, HttpStatus.OK);

        when(restTemplate.getForEntity(url, BaseResponse.class)).thenReturn(response);

        BaseResponse actual = serviceRest.saveGame(gameId);
        verify(restTemplate).getForEntity(url, BaseResponse.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void saveGameWithExceptionTest() {
        UUID gameId = UUID.randomUUID();
        String url = "http://localhost:8080/single_player/game/" + gameId + "/save";
        String exceptionMessage = "TEST";

        when(restTemplate.getForEntity(url, BaseResponse.class)).thenThrow(new RuntimeException(exceptionMessage));
        BaseResponse actual = serviceRest.saveGame(gameId);
        verify(restTemplate).getForEntity(url, BaseResponse.class);
        assertNotNull(actual);
        assertEquals(BaseResponse.Status.FAILURE, actual.getStatus());
        assertTrue(actual.getMessage().contains(exceptionMessage));
    }
}