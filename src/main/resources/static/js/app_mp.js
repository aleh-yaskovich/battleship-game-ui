var stompClient = null;
var gameModel = {};
var playerId = null;
var outputMessage = {};

function getGameModel(gameModel) {

    playerId = gameModel.playerModel.playerId;

    var socket = new SockJS('http://localhost:8080/ws-connect');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function() {
        stompClient.subscribe(
          '/topic/game/' + gameModel.gameId +'/player/'+ playerId + '/update', function(gameModelUI) {
              updateGameModel(JSON.parse(gameModelUI.body));
        });
        stompClient.subscribe(
          '/topic/game/' + gameModel.gameId +'/player/'+ playerId, function(gameModelUI) {
              getNewGameModel(JSON.parse(gameModelUI.body));
        });
        stompClient.subscribe(
          '/topic/game/' + gameModel.gameId + "/messenger", function(outputMessage) {
              getMessage(JSON.parse(outputMessage.body));
        });
    });

    setNamesOfPlayers();
    lockEnemyBattleField();

    if(gameModel.enemyModel.playerId == null) {
        showPreparingModal("Wait for the second player...");
    } else {
        setTimeout(() => {stompClient.send("/app/game/" + gameModel.gameId, {}); }, 500);
    }
}

function getNewGameModel(gameModelUI) {
    gameModel = gameModelUI;
    setNamesOfPlayers();
    showPreparingModal("Your opponent in the game is the player "+gameModel.enemyModel.playerName.toUpperCase()
        +". You can start to play!");
    changeActiveBattleField();
}

function updateGameModel(gameModelUI) {
    gameModel = gameModelUI;
    updateBattleFields();
    changeActiveBattleField();
    endOfTheGame();
}

function playerMakesShot(shot) {
    event.preventDefault();
    var gameId = gameModel.gameId;
    var playerId = gameModel.playerModel.playerId;
    stompClient.send("/app/game/"+gameId+"/player/"+playerId+"/shot/"+shot, {});
}

function getMessage(outputMessage) {
    var dialog = document.getElementById('dialog');
    var p = document.createElement('p');
    if(gameModel.playerModel.playerId == outputMessage.playerId) {
      p.setAttribute('id','_dialog1');
    } else {
      p.setAttribute('id','_dialog2');
    }
    var span = document.createElement('span');
    var info = document.createTextNode(outputMessage.playerName+' ['+outputMessage.time+']: ');
    span.appendChild(info);
    var strong = document.createElement('strong');
    var text = document.createTextNode(outputMessage.text);
    strong.appendChild(text);
    p.appendChild(span);
    p.appendChild(strong);
    dialog.appendChild(p);
}

function sendMessage() {
    event.preventDefault();
    var text = document.getElementById('text').value;
    stompClient.send("/app/game/"+gameModel.gameId+"/messenger", {},
        JSON.stringify(
            {'playerId':gameModel.playerModel.playerId, 'playerName':gameModel.playerModel.playerName, 'text':text}
        ));
    document.getElementById('text').value = "";
}

function showPreparingModal(text) {
    document.getElementById('_text').innerHTML = text;
    const elemModal = document.querySelector('#preparingModal');
    const modal = new bootstrap.Modal(elemModal);
    modal.show();
}

function changeActiveBattleField() {
    if(gameModel.playerModel.playerId == gameModel.activePlayer) {
        unlockEnemyBattleField();
    } else {
        lockEnemyBattleField();
    }
}

function setNamesOfPlayers() {
    document.getElementById("player_name").innerHTML = gameModel.playerModel.playerName.toUpperCase();
    document.getElementById("enemy_name").innerHTML = gameModel.enemyModel.playerName.toUpperCase();
}
