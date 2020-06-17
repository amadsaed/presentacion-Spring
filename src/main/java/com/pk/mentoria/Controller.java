package com.pk.mentoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //Un metodo para hacer web request para obtener JSON de los juegos y sus jugadores
    @RequestMapping("/games")
    public  Map<String,Object>getGames(Authentication authentication){
        Map<String,Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)){
            dto.put("player","Guest");
        }
        else {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.getPlayerDTO());
        }

        dto.put("games",gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));
        return dto;
    }

    //Un metodo para hacer web request para obtener JSON de un juego
    @RequestMapping("/game_view/{id}")
    public  Map<String, Object> getGameView (@PathVariable long id){

        GamePlayer gamePlayer = gamePlayerRepository.findById(id).get();

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", gamePlayer.getGame().getId());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayersList());

        return dto;
    }

    //Un metodo para hacer web request para registrarse
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(username) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //Un metodo para hacer web request para crear un juego nuevo
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(MakeMap("error", "No player logged in"), HttpStatus.FORBIDDEN);
        }
        else {
        Game game = new Game();
        gameRepository.save(game);

        Player player = playerRepository.findByUserName(authentication.getName());

        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game));
        return new ResponseEntity<>(MakeMap("game_player_id", gamePlayer.getId()), HttpStatus.CREATED);
        }

    }

    //Un metodo para hacer web request para unirse a un juego
    @RequestMapping(path = "/games/{id}/players")
    public ResponseEntity<Map<String, Object>> joinGame (Authentication authentication, @PathVariable long id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(MakeMap("error", "There is no game"), HttpStatus.FORBIDDEN);
        }

        if (isGuest(authentication)) {
            return new ResponseEntity<>(MakeMap("error", "There is no player"), HttpStatus.FORBIDDEN);
        }

        if  (game.getGamePlayers().stream().count()>1){
            return new ResponseEntity<>(MakeMap("error", "full players"), HttpStatus.FORBIDDEN);
        }

        if (game.getGamePlayers().stream().map(gamePlayer -> gamePlayer.getPlayer().getUserName()).collect(Collectors.toList()).contains(authentication.getName())){
            return new ResponseEntity<>(MakeMap("error", "You are in your game !!"),HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = new GamePlayer( player,  game);
        gamePlayerRepository.save(gamePlayer);

        return new ResponseEntity<>(MakeMap("game_player_id", gamePlayer.getId()), HttpStatus.CREATED);
    }

    //Un metodo booleano para verificar si el usuario esta logueado
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //Un metodo para crear un mapa tipo LinkedHashMap
    public Map<String,Object> MakeMap (String key, Object value){
        Map<String, Object> createMap = new LinkedHashMap<>();
        createMap.put (key, value);
        return createMap;
    }
}
