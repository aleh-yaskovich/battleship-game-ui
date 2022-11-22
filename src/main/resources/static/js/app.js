var stompClient = null;
var gameModel = {};

function getGameModel(gameModelUI) {
    gameModel = gameModelUI;
    var playerBattleField = gameModel.playerModelUI.battleField;
    var enemyBattleField = gameModel.enemyModelUI.battleField
    var numberOfPlayerShips = gameModel.playerModelUI.sizeOfShips;
    var numberOfEnemyShips = gameModel.enemyModelUI.sizeOfShips;

    document.getElementById("player_name").innerHTML = gameModel.playerModelUI.playerName.toUpperCase();
    document.getElementById("enemy_name").innerHTML = gameModel.enemyModelUI.playerName.toUpperCase();

    updatePlayerBattlefield(playerBattleField);
    updateEnemyBattlefield(enemyBattleField);
}

function makeHit(gameModelUI) {
    gameModel = gameModelUI;
    blockEnemyBattleField();

    var playerBattleField = gameModel.playerModelUI.battleField;
    var enemyBattleField = gameModel.enemyModelUI.battleField;
    document.getElementById("_player").innerHTML = ""+gameModel.playerModelUI.sizeOfShips;
    document.getElementById("_enemy").innerHTML = ""+gameModel.enemyModelUI.sizeOfShips;

    updatePlayerBattlefield(playerBattleField);
    updateEnemyBattlefield(enemyBattleField);

    if(gameModel.botStatus) {
        botDoHit();
    }

    if(gameModel.playerModelUI.sizeOfShips == 0 || gameModel.enemyModelUI.sizeOfShips == 0) {
        if(gameModel.playerModelUI.sizeOfShips == 0) {
          showModal(gameModel.enemyModelUI.playerName);
        } else {
          showModal(gameModel.playerModelUI.playerName);
        }
    }
}

function playerDoHit(hit) {
    event.preventDefault();
    var gameId = gameModel.gameModelId;
    stompClient.send("/app/game/" + gameId + "/" + hit, {});
}

function botDoHit() {
    var element = document.getElementById('enemy_battlefield');
    if(element !== null) {
        element.removeAttribute('id');
        element.setAttribute('id', 'dis');
    }
    var gameId = gameModel.gameModelId;
    setTimeout(() => { stompClient.send("/app/game/" + gameId + "/bot", {}); }, 500);
}

function updatePlayerBattlefield(playerBattleField) {
    var playerElements = document.querySelectorAll('div.fieldPoint');
    for (var i = 0; i < playerElements.length; i++) {
       playerElements[i].setAttribute("id", "point"+playerBattleField[i]);
    }
}

function updateEnemyBattlefield(enemyBattleField) {
    var botElements = document.querySelectorAll('a.fieldPoint');
    for (var n = 0; n < botElements.length; n++) {
        botElements[n].setAttribute("id", "point"+enemyBattleField[n]);
    }
}

function showModal(name) {
    var congratulation = "Player "+ name.toUpperCase() +" won!";
    document.getElementById('congratulation').innerHTML = congratulation;
    const elemModal = document.querySelector('#exampleModal');
    const modal = new bootstrap.Modal(elemModal);
    modal.show();
}

function blockEnemyBattleField() {
    if(!gameModel.botStatus) {
        var element = document.getElementById('dis');
        if(element !== null) {
            element.removeAttribute('id');
            element.setAttribute('id','enemy_battlefield');
        }
    }
}


