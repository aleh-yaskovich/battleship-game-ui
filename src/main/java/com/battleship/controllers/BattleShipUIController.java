package com.battleship.controllers;

import com.battleship.models.GameModelUI;
import com.battleship.models.PreparingModel;
import com.battleship.services.BattleShipServiceRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String getEmptyBattleField(Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModelUI() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
        } else {
            model.addAttribute("preparingModel",
                    new PreparingModel(
                            gameModelUI.getPlayerModelUI().getPlayerId(),
                            gameModelUI.getPlayerModelUI().getPlayerName()
                    )
            );
            model.addAttribute("points", gameModelUI.getPlayerModelUI().getBattleField());
        }
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        return "single_player";
    }

    @PostMapping("/single_player/random_battlefield")
    public String getRandomBattleField(PreparingModel preparingModel, Model model) {
        gameModelUI = serviceRest.getRandomBattleFieldModel(preparingModel);
        model.addAttribute("points", gameModelUI.getPlayerModelUI().getBattleField());
        model.addAttribute("literals", LITERALS);
        return "redirect:/single_player";
    }

    @GetMapping("/single_player/game")
    public String getSinglePlayerGame(Model model) {
        if(gameModelUI == null || gameModelUI.getPlayerModelUI() == null) {
            model.addAttribute("preparingModel", new PreparingModel());
            model.addAttribute("points", new int[100]);
            return "redirect:/single_player";
        }
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points", gameModelUI.getPlayerModelUI().getBattleField());
        model.addAttribute("literals", LITERALS);
        model.addAttribute("gameModel", gameModelUI);
        return "single_player_game";
    }

    @GetMapping("/single_player/restart")
    public String backToSinglePlayer() {
        UUID gameModelId = gameModelUI.getGameModelId();
        serviceRest.deleteGameModel(gameModelId);
        gameModelUI = null;
        return "redirect:/single_player";
    }
}
