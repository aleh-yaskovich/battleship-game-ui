var stompClient = null;
var gameModel = {};

function getGameModel(gameModelUI) {
    var socket = new SockJS('http://localhost:8080/ws-connect');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function() {
        stompClient.subscribe('/topic/game', function(gameModelUI) {
            makeShot(JSON.parse(gameModelUI.body));
        });
    });

    gameModel = gameModelUI;
    updateBattleFields();
    document.getElementById("player_name").innerHTML = gameModel.playerModel.playerName.toUpperCase();
    document.getElementById("enemy_name").innerHTML = gameModel.enemyModel.playerName.toUpperCase();
}

function makeShot(gameModelUI) {
    gameModel = gameModelUI;
    updateBattleFields();
    if(gameModel.activePlayer == gameModel.enemyModel.playerId) {
        botMakesShot();
    } else {
        unlockEnemyBattleField();
    }
    endOfTheGame();
}

function playerMakesShot(shot) {
    event.preventDefault();
    var gameId = gameModel.gameId;
    stompClient.send("/app/game/" + gameId + "/" + shot, {});
}

function botMakesShot() {
    var element = document.getElementById('enemy_battlefield');
    lockEnemyBattleField();
    var gameId = gameModel.gameId;
    setTimeout(() => { stompClient.send("/app/game/" + gameId + "/bot", {}); }, 500);
}


