package com.battleship.services.integration;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.models.response.BaseResponse;
import com.battleship.models.response.GameModelUIResponse;
import com.battleship.services.BattleShipServiceRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BattleShipServiceRestIT {

    @Autowired
    private BattleShipServiceRest serviceRest;
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getRandomBattleFieldModelForSinglePlayerTest() throws Exception {
        String active = "single_player";
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        String preparingModelJson = mapper.writeValueAsString(preparingModel);

        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        GameModelUIResponse expected = GameModelUIResponse.builder()
                .gameModelUI(gameModelUI)
                .status(BaseResponse.Status.SUCCESS)
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/single_player/random_battlefield")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(preparingModelJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        GameModelUIResponse actual = serviceRest.getRandomBattleFieldModel(preparingModel, active);
        mockServer.verify();
        assertNotNull(actual);
        assertEquals(gameModelUI, actual.getGameModelUI());
    }

    @Test
    void getRandomBattleFieldModelForMultiplayerTest() throws Exception {
        String active = "multiplayer";
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        String preparingModelJson = mapper.writeValueAsString(preparingModel);

        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        GameModelUIResponse expected = GameModelUIResponse.builder()
                .gameModelUI(gameModelUI)
                .status(BaseResponse.Status.SUCCESS)
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/multiplayer/random_battlefield")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(preparingModelJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        GameModelUIResponse actual = serviceRest.getRandomBattleFieldModel(preparingModel, active);
        mockServer.verify();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void deleteGameModelTest() throws Exception {
        UUID gameModelId = UUID.randomUUID();
        BaseResponse expected = BaseResponse.builder().status(BaseResponse.Status.SUCCESS).build();

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8080/single_player/game/" + gameModelId + "/delete")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        BaseResponse actual = serviceRest.deleteGameModel(gameModelId);
        assertNotNull(actual);
        assertEquals(expected, actual);
        mockServer.verify();
    }

    @Test
    void getFreeGamesListTest() throws Exception {
        UUID playerId = UUID.randomUUID();
        FreeGame freeGame = new FreeGame(UUID.randomUUID(), "Name");
        List<FreeGame> expected = List.of(freeGame);

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/multiplayer/free_games?withoutId="+playerId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        List<FreeGame> actual = serviceRest.getFreeGamesList(playerId);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        FreeGame actualFreeGame = mapper.convertValue(actual.get(0), FreeGame.class);
        assertEquals(freeGame, actualFreeGame);
        mockServer.verify();
    }

    @Test
    void joinToMultiplayerGameTest() throws Exception {
        UUID gameId = UUID.randomUUID();
        String gameModelUIJson = mapper.writeValueAsString(new GameModelUI());
        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        GameModelUIResponse expected = GameModelUIResponse.builder()
                .gameModelUI(gameModelUI)
                .status(BaseResponse.Status.SUCCESS)
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/multiplayer/game/"+gameId+"/join")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(gameModelUIJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        GameModelUIResponse actual = serviceRest.joinToMultiplayerGame(gameId, new GameModelUI());
        mockServer.verify();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void saveGameTest() throws Exception {
        UUID gameModelId = UUID.randomUUID();
        BaseResponse expected = BaseResponse.builder().status(BaseResponse.Status.SUCCESS).build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/single_player/game/" + gameModelId + "/save")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        BaseResponse actual = serviceRest.saveGame(gameModelId);
        assertNotNull(actual);
        assertEquals(expected, actual);
        mockServer.verify();
    }
}