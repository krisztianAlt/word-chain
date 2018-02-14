var app = app || {};

app.init = function() {
    app.onlineEntitiesHandler.getOnlineEntitiesTimer();
    app.onlineEntitiesHandler.newMatchButton();
};

app.onlineEntitiesHandler = {

    getOnlineEntitiesTimer: function () {
        setInterval(getOnlineEntities, 3000);

        function getOnlineEntities(){
            $.ajax({
                url: '/api/get-online-entities',
                method: 'GET',
                dataType: 'json',
                success: function(onlineEntities) {
                    var ownId = onlineEntities.ownId;

                    var playerList = onlineEntities.onlinePlayers;
                    app.onlineEntitiesHandler.refreshPlayerList(playerList);

                    var myGames = onlineEntities.myNewGames;
                    var othersGames = onlineEntities.othersNewGames;
                    var everyGame = onlineEntities.everyNewGame;

                    if (everyGame != null){
                        app.onlineEntitiesHandler.refreshEveryGameList(everyGame);
                    } else {
                        app.onlineEntitiesHandler.refreshMyGameList(myGames);
                        app.onlineEntitiesHandler.refreshOthersGamesList(othersGames, ownId);
                    }
                },
                error: function() {
                    console.log('ERROR: API calling failed.');
                }
            });
        }
    },

    refreshPlayerList: function (playerList) {

        // sorting by alphabetical order of user names:
        playerList.sort(function(a, b){
            var x = a.userName.toLowerCase();
            var y = b.userName.toLowerCase();
            if (x < y) {return -1;}
            if (x > y) {return 1;}
            return 0;
        });

        // delete previous state of the table:
        var deletePlayerRows = document.getElementsByClassName('players-table-body-row');
        while (deletePlayerRows.length > 0){
            deletePlayerRows[0].remove();
        }

        // add new rows to the table:
        var tableBody = document.getElementById('players-table-body');

        for (playerIndex = 0; playerIndex < playerList.length; playerIndex++){
            var newRow = document.createElement('tr');
            newRow.className = 'players-table-body-row';

            var playerName = document.createElement('td');
            var playerNameText = document.createTextNode(playerList[playerIndex].userName);
            playerName.appendChild(playerNameText);

            newRow.appendChild(playerName);
            tableBody.appendChild(newRow);
        }

    },

    refreshEveryGameList: function (everyGame) {

        // delete previous state of the table
        var deleteGameRows = document.getElementsByClassName('every-game-table-body-row');
        while (deleteGameRows.length > 0){
            deleteGameRows[0].remove();
        }

        // add new rows to the table
        var tableBody = document.getElementById('every-game-table-body');

        for (gameIndex = 0; gameIndex < everyGame.length; gameIndex++){
            var newRow = document.createElement('tr');
            newRow.className = 'every-game-table-body-row';

            var creatorName = document.createElement('td');
            var creatorNameText = document.createTextNode(everyGame[gameIndex].creatorName);
            creatorName.appendChild(creatorNameText);

            var members = document.createElement('td');
            var memberList = document.createElement('ul');
            for (memberIndex = 0; memberIndex < everyGame[gameIndex].players.length; memberIndex++){
                var memberName = document.createElement('li');
                var memberNameText = document.createTextNode(everyGame[gameIndex].players[memberIndex].userName);
                memberName.appendChild(memberNameText);
                memberList.appendChild(memberName);
            }
            members.appendChild(memberList);

            var gameType = document.createElement('td');
            var gameTypeText = document.createTextNode(everyGame[gameIndex].gameType);
            gameType.appendChild(gameTypeText);

            newRow.appendChild(creatorName);
            newRow.appendChild(members);
            newRow.appendChild(gameType);
            tableBody.appendChild(newRow);
        }
    },

    refreshMyGameList: function (myGames){
        // delete previous state of the table
        var deleteGameRows = document.getElementsByClassName('my-games-table-body-row');
        while (deleteGameRows.length > 0){
            deleteGameRows[0].remove();
        }

        // add new rows to the table:
        var tableBody = document.getElementById('my-games-table-body');

        for (gameIndex = 0; gameIndex < myGames.length; gameIndex++){
            var newRow = document.createElement('tr');
            newRow.className = 'my-games-table-body-row';

            var members = document.createElement('td');
            var memberList = document.createElement('ul');
            for (memberIndex = 0; memberIndex < myGames[gameIndex].players.length; memberIndex++){
                var memberName = document.createElement('li');
                var memberNameText = document.createTextNode(myGames[gameIndex].players[memberIndex].userName);
                memberName.appendChild(memberNameText);
                memberList.appendChild(memberName);
            }
            members.appendChild(memberList);

            var gameType = document.createElement('td');
            var gameTypeText = document.createTextNode(myGames[gameIndex].gameType);
            gameType.appendChild(gameTypeText);

            /*var startButtonCell = document.createElement('td');
            var startButton = document.createElement('button');
            startButton.className = 'btn btn-success btn-sm';
            startButton.classList.add('start-button');
            startButton.textContent = 'Start';
            startButtonCell.appendChild(startButton);*/

            // <a th:href="@{'~/' + ${accomodation.getPlanet().getId()}+'/accomodation'}" role="button" class="btn btn-primary"

            var startButtonCell = document.createElement('td');
            var hiddenForm = document.createElement('form');
            hiddenForm.setAttribute('action', '/game');
            hiddenForm.setAttribute('method', 'post');

            var hiddenInputField = document.createElement('input');
            hiddenInputField.setAttribute('name', 'gameid');
            hiddenInputField.setAttribute('type', 'hidden');
            hiddenInputField.setAttribute('value', myGames[gameIndex].gameId);
            hiddenInputField.setAttribute('th:style', "${'visibility: hidden'}");
            hiddenForm.appendChild(hiddenInputField);

            var submitButton = document.createElement('button');
            submitButton.setAttribute('type', 'submit');
            submitButton.className = 'btn btn-success btn-sm';
            submitButton.textContent = 'Start';
            hiddenForm.appendChild(submitButton);

            startButtonCell.appendChild(hiddenForm);

            var deleteButtonCell = document.createElement('td');
            var deleteButton = document.createElement('button');
            deleteButton.className = 'btn btn-danger btn-sm';
            deleteButton.classList.add('delete-button');
            deleteButton.textContent = 'Delete';
            var gameIdAttribute = document.createAttribute("data-gameid");
            gameIdAttribute.value = myGames[gameIndex].gameId;
            deleteButton.setAttributeNode(gameIdAttribute);
            deleteButtonCell.appendChild(deleteButton);

            newRow.appendChild(members);
            newRow.appendChild(gameType);
            newRow.appendChild(startButtonCell);
            newRow.appendChild(deleteButtonCell);
            tableBody.appendChild(newRow);

        }

        app.onlineEntitiesHandler.activateDeleteGameButtons();

    },

    refreshOthersGamesList: function (othersGames, ownId) {

        // delete previous state of the table
        var deleteGameRows = document.getElementsByClassName('other-games-table-body-row');
        while (deleteGameRows.length > 0){
            deleteGameRows[0].remove();
        }

        // add new rows to the table
        var tableBody = document.getElementById('other-games-table-body');

        for (gameIndex = 0; gameIndex < othersGames.length; gameIndex++){
            var newRow = document.createElement('tr');
            newRow.className = 'other-games-table-body-row';

            var creatorName = document.createElement('td');
            var creatorNameText = document.createTextNode(othersGames[gameIndex].creatorName);
            creatorName.appendChild(creatorNameText);

            var members = document.createElement('td');
            var memberList = document.createElement('ul');
            var playerJoined = false;
            for (memberIndex = 0; memberIndex < othersGames[gameIndex].players.length; memberIndex++){
                var memberName = document.createElement('li');
                var memberNameText = document.createTextNode(othersGames[gameIndex].players[memberIndex].userName);
                memberName.appendChild(memberNameText);
                memberList.appendChild(memberName);
                if (othersGames[gameIndex].players[memberIndex].playerId == ownId){
                    playerJoined = true;
                }
            }
            members.appendChild(memberList);

            var gameType = document.createElement('td');
            var gameTypeText = document.createTextNode(othersGames[gameIndex].gameType);
            gameType.appendChild(gameTypeText);

            var buttonCell = document.createElement('td');
            var button = document.createElement('button');

            var gameIdAttribute = document.createAttribute("data-gameid");
            gameIdAttribute.value = othersGames[gameIndex].gameId;
            button.setAttributeNode(gameIdAttribute);

            if (playerJoined && othersGames[gameIndex].gameStatus === 'PREPARATION'){
                var hiddenForm = document.createElement('form');
                hiddenForm.setAttribute('action', '/game');
                hiddenForm.setAttribute('method', 'post');

                var hiddenInputField = document.createElement('input');
                hiddenInputField.setAttribute('name', 'gameid');
                hiddenInputField.setAttribute('type', 'hidden');
                hiddenInputField.setAttribute('value', othersGames[gameIndex].gameId);
                hiddenInputField.setAttribute('th:style', "${'visibility: hidden'}");
                hiddenForm.appendChild(hiddenInputField);

                button.className = 'btn btn-success btn-sm';
                button.classList.add('start-button');
                button.textContent = 'Enter game';
                hiddenForm.appendChild(button);

                buttonCell.appendChild(hiddenForm);

            } else if (playerJoined && othersGames[gameIndex].gameStatus === 'NEW'){
                button.className = 'btn btn-danger btn-sm';
                button.classList.add('leave-button');
                button.textContent = 'Leave';
                buttonCell.appendChild(button);
            } else {
                button.className = 'btn btn-info btn-sm';
                button.classList.add('join-button');
                button.textContent = 'Join';
                buttonCell.appendChild(button);
            }

            newRow.appendChild(creatorName);
            newRow.appendChild(members);
            newRow.appendChild(gameType);
            newRow.appendChild(buttonCell);
            tableBody.appendChild(newRow);

        }

        app.onlineEntitiesHandler.activateJoinButtons();
        app.onlineEntitiesHandler.activateLeaveButtons();

    },

    /*newMatchButton: function () {
        try {
            var newMatchButton = document.getElementById('create-new-match');
            newMatchButton.addEventListener('click', function () {
                $.ajax({
                    url: '/api/create-new-match',
                    method: 'GET',
                    success: function() {
                        console.log('SUCCESS: New match created. See the refreshing list.')
                    },
                    error: function() {
                        console.log('ERROR: API calling failed.');
                    }
                });
            });
        } catch (ev){
            // USER NOT LOGGED IN
        }
    },*/

    activateJoinButtons: function () {
        var allJoinButton = document.getElementsByClassName('join-button');
        for (buttonIndex = 0; buttonIndex < allJoinButton.length; buttonIndex++) {
            allJoinButton[buttonIndex].addEventListener('click', function (event) {
                var gameId = $(this).data("gameid");
                var dataPackage = {'gameId': gameId};
                $.ajax({
                    url: '/api/game-join',
                    method: 'POST',
                    data: dataPackage,
                    dataType: 'json',
                    success: function(response) {
                        console.log(response);
                    },
                    error: function(ev) {
                        console.log('ERROR: API calling failed. ' + JSON.stringify(ev));
                    }
                });
            })
        }
    },

    activateLeaveButtons: function () {
        var allLeaveButton = document.getElementsByClassName('leave-button');
        for (buttonIndex = 0; buttonIndex < allLeaveButton.length; buttonIndex++) {
            allLeaveButton[buttonIndex].addEventListener('click', function (event) {
                var gameId = $(this).data("gameid");
                var dataPackage = {'gameId': gameId};
                $.ajax({
                    url: '/api/game-leave',
                    method: 'POST',
                    data: dataPackage,
                    success: function(response) {
                        console.log(response);
                    },
                    error: function(ev) {
                        console.log('ERROR: API calling failed. ' + JSON.stringify(ev));
                    }
                });
            })
        }
    },

    activateDeleteGameButtons: function () {
        var allDeleteButton = document.getElementsByClassName('delete-button');
        for (buttonIndex = 0; buttonIndex < allDeleteButton.length; buttonIndex++) {
            allDeleteButton[buttonIndex].addEventListener('click', function (event) {
                var gameId = $(this).data("gameid");
                var dataPackage = {'gameId': gameId};
                $.ajax({
                    url: '/api/game-delete',
                    method: 'POST',
                    data: dataPackage,
                    success: function(response) {
                        console.log(response);
                    },
                    error: function(ev) {
                        console.log('ERROR: API calling failed. ' + JSON.stringify(ev));
                    }
                });
            })
        }
    }

};

$(document).ready(app.init());