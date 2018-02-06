var app = app || {};

app.init = function() {
    app.onlineEntitiesHandler.getOnlineIntitiesTimer();
    app.onlineEntitiesHandler.newMatchButton();
};

$(document).ready(app.init());