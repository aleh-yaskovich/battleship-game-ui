package com.battleship.controllers;

import com.battleship.models.FreeGame;
import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.services.BattleShipServiceRest;
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
    private GameModelUI gameModelUI;

    @GetMapping("/")
    public String starting() {
        return "index";
    }

    @GetMapping("/single_player")
    public String getSinglePlayerPage(Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModel() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
        } else if(!gameModelUI.getEnemyModel().getPlayerName().equals("Bot")) {
            serviceRest.deleteGameModel(gameModelUI.getGameId());
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

    @PostMapping("/single_player/random_battlefield")
    public String getSinglePlayerPageWithRandomBattleField(PreparingModel preparingModel, Model model) {
        gameModelUI = serviceRest.getRandomBattleFieldModelForSinglePlayer(preparingModel);
        model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("active", "single_player");
        return "redirect:/single_player";
    }

    @GetMapping("/single_player/game")
    public String getSinglePlayerGame(Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModel() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
            return "redirect:/single_player";
        }
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        model.addAttribute("active", "single_player");
        return "single_player_game";
    }

    @GetMapping("/single_player/restart")
    public String backToSinglePlayer() {
        UUID gameModelId = gameModelUI.getGameId();
        serviceRest.deleteGameModel(gameModelId);
        gameModelUI = null;
        return "redirect:/single_player";
    }

    @GetMapping("/multiplayer")
    public String getMultiplayerPage(Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModel() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
        } else if(gameModelUI.getEnemyModel().getPlayerName().equals("Bot")) {
            serviceRest.deleteGameModel(gameModelUI.getGameId());
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

    @PostMapping("/multiplayer/random_battlefield")
    public String getMultiplayerPageWithRandomBattleField(PreparingModel preparingModel, Model model) {
        gameModelUI = serviceRest.getRandomBattleFieldModelForMultiplayer(preparingModel);
        model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("active", "multiplayer");
        return "redirect:/multiplayer";
    }

    @GetMapping("/multiplayer/game")
    public String getMultiplayerGame(Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModel() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
            return "redirect:/multiplayer";
        }
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        model.addAttribute("active", "multiplayer");
        return "multiplayer_game";
    }

    @GetMapping("/multiplayer/game/{gameId}")
    public String joinToMultiplayerGame(@PathVariable UUID gameId, Model model) {
        gameModelUI = serviceRest.joinToMultiplayerGame(gameId, gameModelUI);
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points", gameModelUI.getPlayerModel().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        model.addAttribute("active", "multiplayer");
        return "redirect:/multiplayer/game";
    }

    @GetMapping("/multiplayer/to_single_player/{gameId}")
    public String redirectToSinglePlayer(@PathVariable UUID gameId, Model model) {
        serviceRest.deleteGameModel(gameId);
        gameModelUI = null;
        return "redirect:/single_player";
    }

    @GetMapping("/multiplayer/restart")
    public String backToMultiplayer() {
        UUID gameModelId = gameModelUI.getGameId();
        serviceRest.deleteGameModel(gameModelId);
        gameModelUI = null;
        return "redirect:/multiplayer";
    }
}
