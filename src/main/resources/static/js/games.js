

//la funcion que arma la tabla de Leaderboard
function getLeaderboard() {

  $.get('/api/leaderboard')

    .done(function (data) {

    var ranking = data.sort(function(a, b){

    return b.totalScore - a.totalScore;
    })

    app.players = ranking;

    })

   // .fail(function (jqXHR, textStatus) {

   //   alert('Failed: ' + textStatus);

   // });
}

//la funcion que arma la tabla de los juegos
function getGames(){
 $.get('/api/games')
.done(function(data){
    app.games = data.games;
    app.user = data.player;
    })
}

// VUE
var app = new Vue({
    el:"#app",
    data:{
        players: [],
        games: [],
        user:"",
        username: "",
        password: "",
    },
    methods:{
        joinGame(gameId){
            $.post("/api/games/" + gameId +"/players")
            .done (function (data){

                //window.location.href = '/web/game.html?gp='+data.game_player_id;//
                window.location.href = 'gridModificado.html?gp='+data.game_player_id;

                })
            .fail(function() {
                                  alert("The game is full !");
                          })
        },
        createGame(){

        $.post("/api/games")
        .done (function (data){
           // window.location.href = '/web/game.html?gp='+data.game_player_id;//
           window.location.href = 'gridModificado.html?gp='+data.game_player_id;
            })
        },
        enterGame(gamePlayers){
            var gamePlayerId = 0;

            if(gamePlayers[0].player.email == app.user.email){
                gamePlayerId = gamePlayers[0].id
            }else{
                gamePlayerId = gamePlayers[1].id
            }

            window.location.href = '/web/game.html?gp='+gamePlayerId;
        },
        login(inp)
        {

            var request = {
                  username: app.username,
                  password: app.password
                };

            $.post("/api/login", request)
                  .done(function() {

                    if(inp==1){
                        message = 'Login in successfully';
                    } else {
                        message = 'Sign up and Login in successfully';
                    }
                    Swal.fire({
                      title: message,
                      showCancelButton: false,
                      confirmButtonColor: '#3085d6',
                      cancelButtonColor: '#d33',
                      confirmButtonText: 'OK'
                    }).then((result) => {
                     location.reload();

                    })


                          $("#username").val("");
                          $("#password").val("");
                  })
                  .fail(function() {
                          alert("log in failed");
                  })
        },
        logout()
            {

                $.post("/api/logout")
                      .done(function() {
                           Swal.fire({
                                                 title: 'log out successfully',
                                                 showCancelButton: false,
                                                 confirmButtonColor: '#3085d6',
                                                 cancelButtonColor: '#d33',
                                                 confirmButtonText: 'OK'
                                               }).then((result) => {
                                                location.reload();

                                               })
                              $("#username").val("");
                              $("#password").val("");
                      })
                      .fail(function() {
                              alert("log out failed");
                      })
            },
        signup(){

                      var request = {
                                       username: app.username,
                                       password: app.password
                                     };
                    $.post("/api/players",request)
                    .done(function(){
                        app.login(2);

                    })
                    .fail(function(){
                        alert("sign up failed");
                    })
        },

    },

    created: function(){
        getGames(),
        getLeaderboard()
    }

})

//BORRAR ESTO
function setBarcos (){

 $.post({
     url: "/api/games/players/6/ships",
     data: JSON.stringify([{ shipType: "destroyer", shipLocations: ["A1", "B1", "C1"] }, {shipType: "carrier", shipLocations:["C4", "C5"]}]),
   dataType: "text",
   contentType: "application/json"
 })
 .done(function (response, status, jqXHR) {
   alert( "ship added: " + response );
 })
 .fail(function (jqXHR, status, httpError) {
   alert("Failed to add ship: " + status + " " + httpError)

 })
}
