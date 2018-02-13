var app = app || {};

app.init = function () {
    app.playTheGame.getGameDataTimer()
    // app.playTheGame.addTestButton();
    app.playTheGame.addWordButton();
}

app.playTheGame = {

    getGameDataTimer: function () {
        setInterval(getGameData, 2000);

        function getGameData(){
            $.ajax({
                url: '/api/get-game-data',
                method: 'GET',
                dataType: 'json',
                success: function(gameData) {
                    console.log(gameData.gameId);
                    var gameStatus = gameData.gameStatus;
                    var message = gameData.currentMessage;

                    if (gameStatus === 'PREPARATION'){
                        app.playTheGame.addNewMessage(message);
                    } else if (gameStatus === 'STARTING1'){
                        app.playTheGame.addNewMessage(message);
                        var playerList = gameData.playerTable;
                        var activePlayer = gameData.activePlayer;
                        app.playTheGame.refreshPlayersTable(playerList, activePlayer);
                    } else if (gameStatus === 'STARTING2'){
                        // just we give time for players to read previous message and order table
                    } else if (gameStatus === 'FIRST_STEP'){
                        app.playTheGame.addNewMessage(message);
                        var playerList = gameData.playerTable;
                        var activePlayer = gameData.activePlayer;
                        var ownId = gameData.ownId;
                        app.playTheGame.refreshPlayersTable(playerList, activePlayer);

                        var firstWord = gameData.firstWord;
                        app.playTheGame.addWordToChain(firstWord);
                        app.playTheGame.activateInputFieldAndAddWordButton(activePlayer, ownId);
                    }
                },
                error: function() {
                    console.log('ERROR: API calling failed.');
                }
            });
        }
    },

    /*addTestButton: function () {
        var addTestButton = document.getElementById("add-test");

        addTestButton.addEventListener('click', function () {
            var chainBox = document.getElementById("chain-box");

            var chainList = document.getElementById("chain-list");
            var newWord = document.createElement('li');
            newWord.innerHTML = "here";
            chainList.appendChild(newWord);

            chainBox.scrollTop = chainBox.scrollHeight;
        });
    },*/

    addNewMessage: function (message) {
        // delete previous message
        var deleteMessageParagraphs = document.getElementsByClassName('message-paragraph');
        while (deleteMessageParagraphs.length > 0){
            deleteMessageParagraphs[0].remove();
        }

        // add new message
        var messageSection = document.getElementById('message-section');
        var newParagraph = document.createElement('p');
        newParagraph.className = 'message-paragraph';
        var text = document.createTextNode(message);
        newParagraph.appendChild(text);
        messageSection.appendChild(newParagraph);
    },

    refreshPlayersTable: function (playerList, activePlayer) {
        // delete previous state of the table:
        var deletePlayerRows = document.getElementsByClassName('player-data-table-body-row');
        while (deletePlayerRows.length > 0){
            deletePlayerRows[0].remove();
        }

        // add new rows to the table:
        var tableBody = document.getElementById('player-data-table-body');

        for (playerIndex = 0; playerIndex < playerList.length; playerIndex++){
            var newRow = document.createElement('tr');
            // newRow.className = 'players-table-body-row';
            if (playerList[playerIndex].playerId === activePlayer){
                newRow.setAttribute('class', 'table-info player-data-table-body-row');
            } else {
                newRow.setAttribute('class', 'player-data-table-body-row');
            }


            var playerName = document.createElement('td');
            var playerNameText = document.createTextNode(playerList[playerIndex].userName);
            playerName.appendChild(playerNameText);

            var time = document.createElement('td');
            var timeText = document.createTextNode(playerList[playerIndex].timeResult);
            time.appendChild(timeText);

            newRow.appendChild(playerName);
            newRow.appendChild(time);
            tableBody.appendChild(newRow);
        }
    },

    addWordToChain: function (word) {
        var chainBox = document.getElementById("chain-box");

        var chainList = document.getElementById("chain-list");
        var newWord = document.createElement('li');
        newWord.innerHTML = word;
        chainList.appendChild(newWord);

        chainBox.scrollTop = chainBox.scrollHeight;
    },

    activateInputFieldAndAddWordButton: function (activePlayer, ownId) {
        if (activePlayer === ownId){
            var inputField = document.getElementById('word-input-field');
            inputField.removeAttribute('disabled');
            inputField.setAttribute('placeholder', 'Type here your new word');
            inputField.setAttribute('autofocus', '');

            var addWordButton = document.getElementById('add-word');
            addWordButton.removeAttribute('disabled');

        }
    },

    deactivateInputFieldAndAddWordButton: function (activePlayer, ownId) {
        if (activePlayer === ownId){
            var inputField = document.getElementById('word-input-field');
            inputField.removeAttribute('autofocus');
            inputField.removeAttribute('placeholder');
            inputField.setAttribute('disabled', '');
            inputField.value = '';

            var addWordButton = document.getElementById('add-word');
            addWordButton.setAttribute('disabled', '');

        }
    },

    addWordButton: function () {
        try {
            var addWordButton = document.getElementById('add-word');
            addWordButton.addEventListener('click', function () {
                var newWord = document.getElementById('word-input-field').value;
                app.playTheGame.deactivateInputFieldAndAddWordButton();
                console.log(newWord);
                var dataPackage = {'newWord': newWord};
                $.ajax({
                    url: '/api/add-word',
                    method: 'POST',
                    data: dataPackage,
                    dataType: 'json',
                    success: function(response) {
                        console.log(response)
                    },
                    error: function() {
                        console.log('ERROR: API calling failed.');
                    }
                });
            });
        } catch (ev){
            // USER NOT LOGGED IN
        }
    },

}

$(document).ready(app.init());