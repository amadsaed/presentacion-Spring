package com.pk.mentoria;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    public GamePlayer() {
    }

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public Map<String,Object> makeGamePlayerDTO(){
        Map<String,Object> dto = new LinkedHashMap<String,Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().getPlayerDTO());
        return dto;
    }
}
