var app = app || {};

app.init = function () {
    app.playTheGame.getGameDataTimer()
    // app.playTheGame.addTestButton();
    app.playTheGame.addWordButton();
}

var timerVar;

app.playTheGame = {

    getGameDataTimer: function () {
        setInterval(getGameData, 2000);

        function getGameData(){
            $.ajax({
                url: '/api/get-game-data',
                method: 'GET',
                dataType: 'json',
                success: function(gameData) {
                    var gameId = gameData.gameId;
                    var gameStatus = gameData.gameStatus;
                    var message = gameData.currentMessage;

                    if (gameStatus === 'PREPARATION'){
                        app.playTheGame.addNewInfoMessage(message);
                    } else if (gameStatus === 'STARTING1'){
                        app.playTheGame.addNewInfoMessage(message);
                        var playerList = gameData.playerTable;
                        var activePlayer = gameData.activePlayer;
                        var actualRound = gameData.actualRound;
                        app.playTheGame.refreshPlayersTable(playerList, activePlayer);
                        app.playTheGame.refreshRound(actualRound);
                    } else if (gameStatus === 'STARTING2'){
                        // just we give time for players to read previous message and order table
                    } else if (gameStatus === 'FIRST_STEP' || gameStatus === 'GAMEINPROGRESS_NEXT_PLAYER'){
                        app.playTheGame.addNewInfoMessage(message);
                        var playerList = gameData.playerTable;
                        var activePlayer = gameData.activePlayer;
                        var actualRound = gameData.actualRound;
                        var ownId = gameData.ownId;
                        app.playTheGame.refreshPlayersTable(playerList, activePlayer);
                        app.playTheGame.refreshRound(actualRound);

                        var lastWord = gameData.lastWord;
                        app.playTheGame.addWordToChain(lastWord);
                        app.playTheGame.activateInputFieldAndAddWordButtonAndTimer(activePlayer, ownId, gameId);
                    } else if (gameStatus === 'GAMEINPROGRESS_WAITING_FOR_GOOD_WORD'){
                        app.playTheGame.addNewInfoMessage(message);
                        var playerList = gameData.playerTable;
                        var activePlayer = gameData.activePlayer;
                        var actualRound = gameData.actualRound;
                        var ownId = gameData.ownId;
                        app.playTheGame.refreshPlayersTable(playerList, activePlayer);
                        app.playTheGame.refreshRound(actualRound);

                        var lastWord = gameData.lastWord;
                        app.playTheGame.addWordToChain(lastWord);
                        app.playTheGame.activateInputFieldAndAddWordButtonAndTimer(activePlayer, ownId, gameId);
                    } else if (gameStatus === 'CLOSED'){
                        app.playTheGame.addNewInfoMessage(message);
                        var playerList = gameData.playerTable;
                        var activePlayer = gameData.activePlayer;
                        app.playTheGame.refreshPlayersTable(playerList, activePlayer);
                        var lastWord = gameData.lastWord;
                        app.playTheGame.addWordToChain(lastWord);
                    }

                },
                error: function() {
                    console.log('ERROR: API calling failed.');
                }
            });
        }
    },

    addNewInfoMessage: function (message) {
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

    addNewWordMessage: function (message) {
        // delete previous message
        var deleteMessageParagraphs = document.getElementsByClassName('word-message-paragraph');
        while (deleteMessageParagraphs.length > 0){
            deleteMessageParagraphs[0].remove();
        }

        // add new message
        var messageSection = document.getElementById('word-message-section');
        var newParagraph = document.createElement('p');
        newParagraph.className = 'word-message-paragraph';
        var text = document.createTextNode(message);
        newParagraph.appendChild(text);
        messageSection.appendChild(newParagraph);

        if (message === 'Accepted.'){
            var wordMessageTimerVar = setInterval(wordMessageTimer, 3000);

            function wordMessageTimer() {
                var deleteMessageParagraphs = document.getElementsByClassName('word-message-paragraph');
                while (deleteMessageParagraphs.length > 0){
                    deleteMessageParagraphs[0].remove();
                }
                clearTimeout(wordMessageTimerVar);
            }
        }
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
            var timeText = document.createTextNode(playerList[playerIndex].timeResult + ' sec');
            time.appendChild(timeText);

            newRow.appendChild(playerName);
            newRow.appendChild(time);
            tableBody.appendChild(newRow);
        }
    },

    refreshRound: function (actualRound) {
        var roundParagraph = document.getElementById("round");
        roundParagraph.innerHTML = "Round: " + actualRound;
    },

    addWordToChain: function (word) {
        var chainBox = document.getElementById("chain-box");

        var chainList = document.getElementById("chain-list");

        if (chainList.lastChild.textContent !== word){
            var newWord = document.createElement('li');
            newWord.innerHTML = word;
            chainList.appendChild(newWord);

            chainBox.scrollTop = chainBox.scrollHeight;
        }

    },

    activateInputFieldAndAddWordButtonAndTimer: function (activePlayer, ownId, gameId) {
        if (activePlayer === ownId){
            var inputField = document.getElementById('word-input-field');
            inputField.removeAttribute('disabled');
            inputField.setAttribute('placeholder', 'Type here your new word');
            inputField.focus();

            var addWordButton = document.getElementById('add-word');
            addWordButton.removeAttribute('disabled');

            var gameIdAttribute = document.createAttribute('data-gameId');
            gameIdAttribute.value = gameId;
            addWordButton.setAttributeNode(gameIdAttribute);

            var activePlayerIdAttribute = document.createAttribute('data-activePlayerId');
            activePlayerIdAttribute.value = activePlayer;
            addWordButton.setAttributeNode(activePlayerIdAttribute);

            var ownIdAttribute = document.createAttribute('data-ownId');
            ownIdAttribute.value = ownId;
            addWordButton.setAttributeNode(ownIdAttribute);

            if (document.getElementById("timer").innerHTML === ""){
                document.getElementById("timer").innerHTML = "0 sec";

                timerVar = setInterval(timer, 1000);

                function timer() {
                    var timerParagraph = document.getElementById("timer");
                    var secondData = timerParagraph.innerHTML.substring(0, timerParagraph.innerHTML.length - 4);
                    var newSecondData = Number(secondData) + 1;
                    timerParagraph.innerHTML = newSecondData + " sec";
                }
            }
        }
    },

    deactivateInputFieldAndAddWordButton: function (activePlayer, ownId) {
        if (activePlayer === ownId){
            var inputField = document.getElementById('word-input-field');
            inputField.removeAttribute('placeholder');
            inputField.setAttribute('disabled', '');
            inputField.value = '';

            var addWordButton = document.getElementById('add-word');
            addWordButton.setAttribute('disabled', '');
            addWordButton.removeAttribute('data-gameid');

            clearTimeout(timerVar);
            document.getElementById("timer").innerHTML = "";
        }
    },

    addWordButton: function () {
        try {
            var addWordButton = document.getElementById('add-word');
            addWordButton.addEventListener('click', function () {
                var newWord = document.getElementById('word-input-field').value;
                var gameId = $(this).data("gameid");
                var activePlayerId = $(this).data("activePlayerId");
                var ownId = $(this).data("ownId");
                var timerParagraph = document.getElementById("timer");
                var secondData = timerParagraph.innerHTML.substring(0, timerParagraph.innerHTML.length - 4);
                app.playTheGame.deactivateInputFieldAndAddWordButton(activePlayerId, ownId);
                var dataPackage = {'newWord': newWord, 'gameId': gameId, 'secondData': secondData};
                $.ajax({
                    url: '/api/add-word',
                    method: 'POST',
                    data: dataPackage,
                    dataType: 'json',
                    success: function(response) {
                        app.playTheGame.addNewWordMessage(response.answer);
                        if (response.answer !== 'Accepted.'){
                            app.playTheGame.activateInputFieldAndAddWordButtonAndTimer(activePlayerId, ownId, gameId)
                        }
                    },
                    error: function() {
                        console.log('ERROR: API calling failed.');
                    }
                });
            });
        } catch (ev){
            // USER NOT LOGGED IN
        }
    }

}

$(document).ready(app.init());