  var inpName = document.getElementById('field_playerName');
  var inpId = document.getElementById('field_playerId');
  var btn = document.getElementById('form_button');
  var startGame = document.getElementById('disabled');
  if (!inpName.value) {
    btn.setAttribute('disabled', true);
  }
  if(inpName.value && inpId.value) {
    startGame.removeAttribute('id');
  }
  inpName.oninput = function() {
    if (!inpName.value) {
      btn.setAttribute('disabled', true);
      startGame.setAttribute('id', 'disabled');
    } else {
      btn.removeAttribute('disabled');
      if(inpId.value) {
        startGame.removeAttribute('id');
      }
    }
  }