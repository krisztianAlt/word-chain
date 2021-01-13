var app = app || {};

app.init = function () {
    app.playTheGame.getGameDataTimer()
    // app.playTheGame.addTestButton();
    app.playTheGame.wordInputField();
    app.playTheGame.addWordButton();
}

var timerVar;

app.playTheGame = {

    getGameDataTimer: function () {
        var timer = setInterval(getGameData, 2000);

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
                        clearTimeout(timer);
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

            var letters = document.createElement('td');
            var lettersText = document.createTextNode(playerList[playerIndex].letters);
            letters.appendChild(lettersText);

            newRow.appendChild(playerName);
            newRow.appendChild(time);
            newRow.appendChild(letters);
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
            inputField.setAttribute('placeholder', 'Type here your new word');
            
            var addWordButton = document.getElementById('add-word');
            
            var gameIdAttribute = document.createAttribute('data-gameid');
            gameIdAttribute.value = gameId;
            addWordButton.setAttributeNode(gameIdAttribute);
            var inputFieldGameIdAttribute = document.createAttribute('data-inputfieldgameid');
            inputFieldGameIdAttribute.value = gameId;
            inputField.setAttributeNode(inputFieldGameIdAttribute);
            
            var activePlayerIdAttribute = document.createAttribute('data-activeplayerid');
            activePlayerIdAttribute.value = activePlayer;
            addWordButton.setAttributeNode(activePlayerIdAttribute);
            var inputFieldActivePlayerIdAttribute = document.createAttribute('data-inputfieldactiveplayerid');
            inputFieldActivePlayerIdAttribute.value = activePlayer; 
            inputField.setAttributeNode(inputFieldActivePlayerIdAttribute);

            var ownIdAttribute = document.createAttribute('data-ownid');
            ownIdAttribute.value = ownId;
            addWordButton.setAttributeNode(ownIdAttribute);
            var inputFieldOwnIdAttribute = document.createAttribute('data-inputfieldownid');
            inputFieldOwnIdAttribute.value = ownId;
            inputField.setAttributeNode(inputFieldOwnIdAttribute);

            inputField.removeAttribute('disabled');
            inputField.focus();
            addWordButton.removeAttribute('disabled');
            
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

    wordInputField: function () {
    	try {
            var wordInputField = document.getElementById('word-input-field');
            wordInputField.addEventListener('keypress', function (e) {
            	if (e.key === 'Enter') {
            		var newWord = wordInputField.value;
                    var gameId = $(this).data("inputfieldgameid");
                    var activePlayerId = $(this).data("inputfieldactiveplayerid");
                    var ownId = $(this).data("inputfieldownid");
                    var timerParagraph = document.getElementById("timer");
                    var secondData = timerParagraph.innerHTML.substring(0, timerParagraph.innerHTML.length - 4);
                    app.playTheGame.sendWordToServer(activePlayerId, ownId, newWord, gameId, secondData);
          	    }
            });
        } catch (ev){
            // USER NOT LOGGED IN
        }
    },
    
    addWordButton: function () {
        try {
            var addWordButton = document.getElementById('add-word');
            addWordButton.addEventListener('click', function () {
                var newWord = document.getElementById('word-input-field').value;
                var gameId = $(this).data("gameid");
                var activePlayerId = $(this).data("activeplayerid");
                var ownId = $(this).data("ownid");
                var timerParagraph = document.getElementById("timer");
                var secondData = timerParagraph.innerHTML.substring(0, timerParagraph.innerHTML.length - 4);
                app.playTheGame.sendWordToServer(activePlayerId, ownId, newWord, gameId, secondData);
            });
        } catch (ev){
            // USER NOT LOGGED IN
        }
    },
    
    sendWordToServer: function(activePlayerId, ownId, newWord, gameId, secondData) {
    	console.log(activePlayerId + ' ' + ownId + ' ' + newWord + ' ' + gameId + ' ' + secondData);
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
    }

}

$(document).ready(app.init());