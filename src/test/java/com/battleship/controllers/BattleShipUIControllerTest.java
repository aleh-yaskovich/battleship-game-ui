package com.battleship.controllers;

import com.battleship.models.GameModelUI;
import com.battleship.models.PlayerModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.services.BattleShipServiceRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private final ObjectMapper mapper = new ObjectMapper();

//    @Test
//    void getSinglePlayerPageTest() throws Exception {
//        mockMvc.perform(get("/single_player"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(view().name("single_player"))
//                .andExpect(model().attribute("preparingModel", new PreparingModel()))
//                .andExpect(model().attribute("points", new int[100]))
//                .andExpect(model().attribute("active", "single_player"));
//    }
//
//    @Test
//    void getSinglePlayerPageWithRandomBattleFieldTest() throws Exception {
//        PreparingModel preparingModel = new PreparingModel(null, "Name");
//        String preparingModelJson = mapper.writeValueAsString(preparingModel);
//
//        mockMvc.perform(post("/single_player/random_battlefield")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(preparingModelJson)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/single_player"));
//    }
//
//    @Test
//    void getSinglePlayerGameTest() throws Exception {
//        mockMvc.perform(get("/single_player/game"))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/single_player"));
//    }
//
//    @Test
//    void backToSinglePlayerTest() throws Exception {
//        when(serviceRest.deleteGameModel(any(UUID.class))).thenReturn(true);
//        mockMvc.perform(get("/single_player/restart"))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/single_player"));
//    }
//
//    @Test
//    void getMultiplayerPageTest() throws Exception {
//        mockMvc.perform(get("/multiplayer"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(view().name("multiplayer"))
//                .andExpect(model().attribute("preparingModel", new PreparingModel()))
//                .andExpect(model().attribute("points", new int[100]))
//                .andExpect(model().attribute("active", "multiplayer"));
//    }
//
//    @Test
//    void getMultiplayerPageWithRandomBattleFieldTest() throws Exception {
//        PreparingModel preparingModel = new PreparingModel(null, "Name");
//        String preparingModelJson = mapper.writeValueAsString(preparingModel);
//
//        mockMvc.perform(post("/multiplayer/random_battlefield")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(preparingModelJson)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/multiplayer"));
//    }
//
//    @Test
//    void getMultiplayerGameTest() throws Exception {
//        mockMvc.perform(get("/multiplayer/game"))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/multiplayer"));
//    }
//
//    @Test
//    void joinToMultiplayerGameTest() throws Exception {
//        UUID gameId = UUID.randomUUID();
//        GameModelUI gameModelUI = getGameModelUI();
//
//        when(serviceRest.joinToMultiplayerGame(gameId, null)).thenReturn(gameModelUI);
//        mockMvc.perform(get("/multiplayer/game/"+gameId))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/multiplayer/game"));
//        verify(serviceRest).joinToMultiplayerGame(gameId, null);
//    }
//
//    @Test
//    void redirectToSinglePlayerTest() throws Exception {
//        UUID gameId = UUID.randomUUID();
//        when(serviceRest.deleteGameModel(gameId)).thenReturn(true);
//        mockMvc.perform(get("/multiplayer/to_single_player/"+gameId))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/single_player"));
//        verify(serviceRest).deleteGameModel(gameId);
//    }
//
//    @Test
//    void backToMultiplayer()throws Exception {
//        when(serviceRest.deleteGameModel(any(UUID.class))).thenReturn(true);
//        mockMvc.perform(get("/multiplayer/restart"))
//                .andDo(print())
//                .andExpect(status().isFound())
//                .andExpect(view().name("redirect:/multiplayer"));
//    }

    private GameModelUI getGameModelUI() {
        int[] battleField = new int[100];
        battleField[0] = 1;
        PlayerModelUI playerModelUI = new PlayerModelUI(UUID.randomUUID(), "Player", 10, battleField);
        PlayerModelUI enemyModelUI = new PlayerModelUI(UUID.randomUUID(), "Enemy", 10, battleField);
        UUID activePlayer = playerModelUI.getPlayerId();
        return new GameModelUI(UUID.randomUUID(), playerModelUI, enemyModelUI, activePlayer);
    }
}
