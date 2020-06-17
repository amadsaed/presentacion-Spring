package com.pk.mentoria;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Game() {
    }

    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Map<String,Object> makeGameDTO(){
        Map<String,Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("gamePlayers", this.getGamePlayersList());
        return dto;
    }

    public List<Map<String,Object>> getGamePlayersList(){
        return this.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.makeGamePlayerDTO())
                .collect(Collectors.toList());
    }
}