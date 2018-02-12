var app = app || {};

app.init = function () {
    app.playTheGame.getGameDataTimer()
    app.playTheGame.addTestButton();
}

app.playTheGame = {

    getGameDataTimer: function () {
        /*setInterval(getGameData, 3000);

        function getGameData(){
            $.ajax({
                url: '/api/get-online-entities',
                method: 'GET',
                dataType: 'json',
                success: function(gameData) {

                },
                error: function() {
                    console.log('ERROR: API calling failed.');
                }
            });
        }*/
    },

    addTestButton: function () {
        var addTestButton = document.getElementById("add-test");

        addTestButton.addEventListener('click', function () {
            var chainBox = document.getElementById("chain-box");

            var chainList = document.getElementById("chain-list");
            var newWord = document.createElement('li');
            newWord.innerHTML = "here";
            chainList.appendChild(newWord);

            chainBox.scrollTop = chainBox.scrollHeight;
        });
    }
}

$(document).ready(app.init());