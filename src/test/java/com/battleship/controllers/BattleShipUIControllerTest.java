package com.battleship.controllers;

import com.battleship.models.GameModelUI;
import com.battleship.models.PlayerModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.models.SavingGame;
import com.battleship.models.response.BaseResponse;
import com.battleship.models.response.GameModelUIResponse;
import com.battleship.services.BattleShipServiceRest;
import com.battleship.services.KafkaConsumerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BattleShipUIController.class)
class BattleShipUIControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BattleShipServiceRest serviceRest;
    @MockBean
    private KafkaConsumerService serviceKafka;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc1;

    @Test
    void getStartPageTest() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getSinglePlayerPageTest() throws Exception {
        mockMvc.perform(get("/single_player"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("single_player"))
                .andExpect(model().attribute("preparingModel", new PreparingModel()))
                .andExpect(model().attribute("points", new int[100]))
                .andExpect(model().attribute("active", "single_player"));
    }

    @Test
    void getMultiplayerPageTest() throws Exception {
        mockMvc.perform(get("/multiplayer"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("multiplayer"))
                .andExpect(model().attribute("preparingModel", new PreparingModel()))
                .andExpect(model().attribute("points", new int[100]))
                .andExpect(model().attribute("active", "multiplayer"));
    }

    @Test
    void joinToMultiplayerGameTest() throws Exception {
        UUID gameId = UUID.randomUUID();
        GameModelUI gameModelUI = getGameModelUI();
        GameModelUIResponse response = GameModelUIResponse.builder()
                .gameModelUI(gameModelUI)
                .status(BaseResponse.Status.SUCCESS)
                .build();
        when(serviceRest.joinToMultiplayerGame(gameId, null)).thenReturn(response);

        mockMvc.perform(get("/multiplayer/game/"+gameId))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/multiplayer/game"));
        verify(serviceRest).joinToMultiplayerGame(gameId, null);
    }

    @Test
    void redirectToSinglePlayerTest() throws Exception {
        UUID gameId = UUID.randomUUID();
        BaseResponse response = BaseResponse.builder().status(BaseResponse.Status.SUCCESS).build();
        when(serviceRest.deleteGameModel(gameId)).thenReturn(response);

        mockMvc.perform(get("/multiplayer/to_single_player/"+gameId))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/single_player"));
        verify(serviceRest).deleteGameModel(gameId);
    }

    @Test
    void showSavedGamesPageTest() throws Exception {
        List<SavingGame> savingGames = List.of(new SavingGame("A vs B", UUID.randomUUID()));
        when(serviceKafka.getSavedGamesFromKafka()).thenReturn(savingGames);

        mockMvc.perform(get("/watching"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("watch_games_list"))
                .andExpect(model().attribute("savingGames", savingGames))
                .andExpect(model().attribute("active", "watching"));
        verify(serviceKafka).getSavedGamesFromKafka();
    }

    @Test
    void showSavedGamesPageIfListOfSavingGamesIsEmptyTest() throws Exception {
        when(serviceKafka.getSavedGamesFromKafka()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/watching"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("watch_games_list"))
                .andExpect(model().attribute("emptySavingGames", true))
                .andExpect(model().attribute("active", "watching"));
        verify(serviceKafka).getSavedGamesFromKafka();
    }

    @Test
    void getListOfGameModelsTest() throws Exception {
        UUID gameId = UUID.randomUUID();
        List<GameModelUI> gameModelUIList = List.of(getGameModelUI());
        when(serviceKafka.getGameModelUIsFromKafka(gameId)).thenReturn(gameModelUIList);

        mockMvc.perform(get("/watching/" + gameId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("watch_game"))
                .andExpect(model().attribute("gameModelUIList", gameModelUIList))
                .andExpect(model().attribute("enemyPoints", new int[100]))
                .andExpect(model().attribute("points", new int[100]))
                .andExpect(model().attribute("active", "watching"));
        verify(serviceKafka).getGameModelUIsFromKafka(gameId);
    }

    @Test
    void getListOfGameModelsIfListOfGameModelUIsIsEmptyTest() throws Exception {
        UUID gameId = UUID.randomUUID();
        List<GameModelUI> gameModelUIList = new ArrayList<>();
        when(serviceKafka.getGameModelUIsFromKafka(gameId)).thenReturn(gameModelUIList);

        mockMvc.perform(get("/watching/" + gameId))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/watching"));
        verify(serviceKafka).getGameModelUIsFromKafka(gameId);
    }

    @Test
    void getRandomBattleFieldForSinglePlayerTest() throws Exception {
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        String preparingModelJson = mapper.writeValueAsString(preparingModel);

        mockMvc.perform(post("/single_player/random_battlefield")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(preparingModelJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/single_player"));
    }

    @Test
    void getRandomBattleFieldForMultiplayerTest() throws Exception {
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        String preparingModelJson = mapper.writeValueAsString(preparingModel);

        mockMvc.perform(post("/multiplayer/random_battlefield")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(preparingModelJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/multiplayer"));
    }

    @Test
    void getGamePageTest() throws Exception {
        mockMvc.perform(get("/multiplayer/game"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/multiplayer"));
    }

    @Test
    void backToPreparingPageTest() throws Exception {
        mockMvc.perform(get("/single_player/restart"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/single_player"));
    }

    @Test
    void saveGameTest() throws Exception {
        UUID gameId = UUID.randomUUID();
        mockMvc.perform(get("/single_player/game/"+ gameId +"/save"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/single_player"));
        verify(serviceRest).saveGame(gameId);
    }

    @Test
    void quitSinglePlayerGameTest() throws Exception {
        mockMvc.perform(get("/single_player/quit"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/single_player"));
    }

    private GameModelUI getGameModelUI() {
        int[] battleField = new int[100];
        battleField[0] = 1;
        PlayerModelUI playerModelUI = new PlayerModelUI(UUID.randomUUID(), "Player", 10, battleField);
        PlayerModelUI enemyModelUI = new PlayerModelUI(UUID.randomUUID(), "Enemy", 10, battleField);
        UUID activePlayer = playerModelUI.getPlayerId();
        return new GameModelUI(UUID.randomUUID(), playerModelUI, enemyModelUI, activePlayer);
    }
}
