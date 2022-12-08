package com.battleship.services;

import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
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
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        String preparingModelJson = mapper.writeValueAsString(preparingModel);

        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/single_player/random_battlefield")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(preparingModelJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        GameModelUI actual = serviceRest.getRandomBattleFieldModelForSinglePlayer(preparingModel);
        mockServer.verify();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void deleteGameModelTest() throws Exception {
        UUID gameModelId = UUID.randomUUID();

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8080/single_player/game/" + gameModelId)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        serviceRest.deleteGameModel(gameModelId);
        mockServer.verify();
    }

    @Test
    void getRandomBattleFieldModelForMultiplayerTest() throws Exception {
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        String preparingModelJson = mapper.writeValueAsString(preparingModel);

        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/multiplayer/random_battlefield")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(preparingModelJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        GameModelUI actual = serviceRest.getRandomBattleFieldModelForMultiplayer(preparingModel);
        mockServer.verify();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void joinToMultiplayerGameTest() throws Exception {
        UUID gameId = UUID.randomUUID();
        String gameModelUIJson = mapper.writeValueAsString(new GameModelUI());
        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/multiplayer/game/"+gameId+"/join")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(gameModelUIJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expected))
                );

        GameModelUI actual = serviceRest.joinToMultiplayerGame(gameId, new GameModelUI());
        mockServer.verify();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}