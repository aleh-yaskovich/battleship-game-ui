var gameModels = [];
var gameModel = {};

function getGameModelsArr() {
    if(gameModels.length !== 0) {
        gameModel = gameModels[0];
        document.getElementById("player_name").innerHTML = gameModel.playerModel.playerName.toUpperCase();
        document.getElementById("enemy_name").innerHTML = gameModel.enemyModel.playerName.toUpperCase();

        var playerElements = document.querySelectorAll('div.playerFieldPoint');
        var enemyElements = document.querySelectorAll('div.enemyFieldPoint');
        var i = 0;
        function myLoop() {
            setTimeout(function() {
                var playerBattleField = gameModels[i].playerModel.battleField;
                var enemyBattleField = gameModels[i].enemyModel.battleField;
                var playerNumberOfShips = gameModels[i].playerModel.sizeOfShips;
                var enemyNumberOfShips = gameModels[i].enemyModel.sizeOfShips;
                for (let k = 0; k < playerElements.length; k++) {
                    playerElements[k].setAttribute("id", "point"+playerBattleField[k]);
                    enemyElements[k].setAttribute("id", "point"+enemyBattleField[k]);
                }
                document.getElementById("_player").innerHTML = ""+playerNumberOfShips;
                document.getElementById("_enemy").innerHTML = ""+enemyNumberOfShips;
                i++;
                if (i < gameModels.length) {
                    myLoop();
                } else {
                    gameModel = gameModels[gameModels.length-1];
                    if(gameModel.playerModel.sizeOfShips == 0) {
                        showModal(gameModel.enemyModel.playerName.toUpperCase());
                    } else {
                        showModal(gameModel.playerModel.playerName.toUpperCase());
                    }
                }
            }, 500);
        }
        myLoop();
    }
}