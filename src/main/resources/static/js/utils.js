function updateBattleFields() {
    var playerBattleField = gameModel.playerModel.battleField;
    var enemyBattleField = gameModel.enemyModel.battleField;
    updatePlayerBattlefield(playerBattleField);
    updateEnemyBattlefield(enemyBattleField);
    document.getElementById("_player").innerHTML = ""+gameModel.playerModel.sizeOfShips;
    document.getElementById("_enemy").innerHTML = ""+gameModel.enemyModel.sizeOfShips;
}

function endOfTheGame() {
    if(gameModel.playerModel.sizeOfShips == 0 || gameModel.enemyModel.sizeOfShips == 0) {
        if(gameModel.playerModel.sizeOfShips == 0) {
            showModal(gameModel.enemyModel.playerName);
        } else {
            showModal(gameModel.playerModel.playerName);
        }
    }
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

function lockEnemyBattleField() {
    var element = document.getElementById('enemy_battlefield');
    if(element !== null) {
        element.removeAttribute('id');
        element.setAttribute('id','dis');
    }
}

function unlockEnemyBattleField() {
    if(gameModel.activePlayer == gameModel.playerModel.playerId) {
        var element = document.getElementById('dis');
        if(element !== null) {
            element.removeAttribute('id');
            element.setAttribute('id','enemy_battlefield');
        }
    }
}

