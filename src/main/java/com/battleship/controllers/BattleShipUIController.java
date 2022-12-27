package com.battleship.controllers;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.models.SavingGame;
import com.battleship.models.response.BaseResponse;
import com.battleship.models.response.GameModelUIResponse;
import com.battleship.services.BattleShipServiceRest;
import com.battleship.services.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class BattleShipUIController {

    private static final char[] LITERALS = {'A','B','C','D','E','F','G','H','I','J'};
    @Autowired
    private BattleShipServiceRest serviceRest;
    @Autowired
    private KafkaConsumerService serviceKafka;
    private GameModelUI gameModelUI;
    private String errorMessage;
    private boolean isError;

    @GetMapping("/")
    public String starting() {
        return "index";
    }

    @GetMapping("/single_player")
    public String getSinglePlayerPage(Model model) {
        showErrorModal(model);
        if(gameModelUI == null || gameModelUI.getPlayerModel() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
        } else if(!gameModelUI.getEnemyModel().getPlayerName().equals("Bot")) {
            BaseResponse response = serviceRest.deleteGameModel(gameModelUI.getGameId());
            if(response.getStatus().equals(BaseResponse.Status.FAILURE)) {
                showErrorModal(model);
            }
            gameModelUI = null;
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
        } else {
            model.addAttribute("preparingModel",
                    new PreparingModel(
                            gameModelUI.getPlayerModel().getPlayerId(),
                            gameModelUI.getPlayerModel().getPlayerName()
                    )
            );
            model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        }
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        model.addAttribute("active", "single_player");
        return "single_player";
    }

    @GetMapping("/multiplayer")
    public String getMultiplayerPage(Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModel() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
        } else if(gameModelUI.getEnemyModel().getPlayerName().equals("Bot")) {
            BaseResponse response = serviceRest.deleteGameModel(gameModelUI.getGameId());
            if(response.getStatus().equals(BaseResponse.Status.FAILURE)) {
                showErrorModal(model);
            }
            gameModelUI = null;
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
        } else {
            model.addAttribute("preparingModel",
                    new PreparingModel(
                            gameModelUI.getPlayerModel().getPlayerId(),
                            gameModelUI.getPlayerModel().getPlayerName()
                    )
            );
            model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
            List<FreeGame> freeGames = serviceRest.getFreeGamesList(gameModelUI.getPlayerModel().getPlayerId());
            if(freeGames != null && !freeGames.isEmpty()) {
                model.addAttribute("freeGames", freeGames);
            } else {
                model.addAttribute("emptyFreeGamesList", true);
            }
        }
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        model.addAttribute("active", "multiplayer");
        return "multiplayer";
    }

    @GetMapping("/multiplayer/game/{gameId}")
    public String joinToMultiplayerGame(@PathVariable UUID gameId, Model model) {
        GameModelUIResponse response = serviceRest.joinToMultiplayerGame(gameId, gameModelUI);
        if(response.getStatus().equals(BaseResponse.Status.SUCCESS)) {
            gameModelUI = response.getGameModelUI();
        } else {
            isError = true;
            errorMessage = response.getMessage();
        }
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        model.addAttribute("active", "multiplayer");
        return "redirect:/multiplayer/game";
    }

    @GetMapping("/multiplayer/to_single_player/{gameId}")
    public String redirectToSinglePlayer(@PathVariable UUID gameId, Model model) {
        BaseResponse response = serviceRest.deleteGameModel(gameId);
        if(response.getStatus().equals(BaseResponse.Status.FAILURE)) {
            showErrorModal(model);
        }
        gameModelUI = null;
        return "redirect:/single_player";
    }

    @GetMapping("watching")
    public String showSavedGamesPage(Model model) {
        List<SavingGame> savingGames = serviceKafka.getSavedGamesFromKafka();
        if(savingGames != null && !savingGames.isEmpty()) {
            model.addAttribute("savingGames", savingGames);
        } else {
            model.addAttribute("emptySavingGames", true);
        }
        model.addAttribute("active", "watching");
        return "watch_games_list";
    }

    @GetMapping("watching/{gameId}")
    public String getListOfGameModels(@PathVariable UUID gameId, Model model) {
        List<GameModelUI> gameModelUIList = serviceKafka.getGameModelUIsFromKafka(gameId);
        model.addAttribute("gameModelUIList", gameModelUIList);
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points",  new int[100]);
        model.addAttribute("literals", LITERALS);
        model.addAttribute("active", "watching");
        return "watch_game";
    }

    @PostMapping("/{active}/random_battlefield")
    public String getRandomBattleField(
            @PathVariable String active, PreparingModel preparingModel, Model model) {
        if(preparingModel == null || preparingModel.getPlayerName() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
            return "redirect:/single_player";
        }
        GameModelUIResponse response = serviceRest.getRandomBattleFieldModel(preparingModel, active);
        if(response.getStatus().equals(BaseResponse.Status.SUCCESS)) {
            gameModelUI = response.getGameModelUI();
            model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        } else {
            isError = true;
            errorMessage = response.getMessage();
            model.addAttribute("points", new int[100]);
        }
        model.addAttribute("literals", LITERALS);
        model.addAttribute("active", "single_player");
        return "redirect:/" + active;
    }

    @GetMapping("/{active}/game")
    public String getGamePage(@PathVariable String active, Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModel() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
            return "redirect:/" + active;
        }
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        model.addAttribute("active", active);
        return active + "_game";
    }

    @GetMapping("/{active}/restart")
    public String backToPreparingPage(@PathVariable String active) {
        if(gameModelUI != null) {
            UUID gameModelId = gameModelUI.getGameId();
            serviceRest.deleteGameModel(gameModelId);
        }
        gameModelUI = null;
        return "redirect:/" + active;
    }

    @GetMapping("{active}/game/{gameId}/save")
    public String saveGame(@PathVariable String active, @PathVariable UUID gameId) {
        serviceRest.saveGame(gameId);
        if(gameModelUI != null) {
            UUID gameModelId = gameModelUI.getGameId();
            serviceRest.deleteGameModel(gameModelId);
        }
        gameModelUI = null;
        return "redirect:/" + active;
    }

    @GetMapping("/{active}/quit")
    public String quitSinglePlayerGame(@PathVariable String active, Model model) {
        BaseResponse response = serviceRest.deleteGameModel(gameModelUI.getGameId());
        if(response.getStatus().equals(BaseResponse.Status.FAILURE)) {
            showErrorModal(model);
        }
        if(gameModelUI != null) {
            UUID gameModelId = gameModelUI.getGameId();
            serviceRest.deleteGameModel(gameModelId);
        }
        gameModelUI = null;
        return "redirect:/" + active;
    }

    private void showErrorModal(Model model) {
        if(isError) {
            model.addAttribute("errorModal", "true");
            model.addAttribute("errorMessage", errorMessage);
            isError = false;
            errorMessage = null;
        }
    }
 }
