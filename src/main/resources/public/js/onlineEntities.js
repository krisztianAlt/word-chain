var app = app || {};

app.onlineEntitiesHandler = {

    getOnlineIntitiesTimer: function () {
        setInterval(getOnlineEntities, 3000);

        function getOnlineEntities(){
            $.ajax({
                url: '/api/get-online-entities',
                method: 'GET',
                success: function(onlineEntities) {
                    var playerList = JSON.parse(onlineEntities).onlinePlayers;
                    app.onlineEntitiesHandler.refreshPlayerList(playerList);

                    var myGames = JSON.parse(onlineEntities).myNewGames;
                    var othersGames = JSON.parse(onlineEntities).othersNewGames;
                    var everyGame = JSON.parse(onlineEntities).everyNewGame;
                    if (everyGame != null){
                        // USER NOT LOGGED IN, LIST EVERY GAME WITHOUT ANY BUTTONS
                    } else {
                        // USER LOGGED IN, PUT ON THE SCREEN THE CREATE NEW MATCH BUTTON,
                        // LIST HIS/HER GAMES WITH START AND DELETE BUTTONS,
                        // AND LIST OTHER PLAYER'S GAMES WITH JOIN BUTTON
                    }

                },
                error: function() {
                    console.log('ERROR: API calling failed.');
                }
            });
        }
    },

    refreshPlayerList: function (playerList) {
        for (index = 0; index < playerList.length; index++) {
            console.log(playerList[index].userName);
        };

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

    newMatchButton: function () {
        var newMatchButton = document.getElementById('create-new-match');
        newMatchButton.addEventListener('click', function () {
            console.log('HI');
        });
    }
    
};