package com.battleship.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BattleShipUIController {

    private static final char[] LITERALS = {'A','B','C','D','E','F','G','H','I','J'};

    @GetMapping("/")
    public String starting() {
        return "index";
    }

    @GetMapping("/single_player")
    public String getEmptyBattleField(Model model) {
        model.addAttribute("points", new int[100]);
        model.addAttribute("literals", LITERALS);
        return "single_player";
    }

    @GetMapping("/single_player/game")
    public String getSinglePlayerGame(Model model) {
        model.addAttribute("enemyPoints", new int[100]);
        model.addAttribute("points", new int[100]);
        model.addAttribute("literals", LITERALS);
        return "single_player_game";
    }
}
