package com.pk.mentoria;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class  Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    private String password ;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Player() { }

    public Player(String userName,String password) {
        this.userName = userName;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public String getPassword() {
        return password;
    }

    public Map<String,Object> getPlayerDTO(){
        Map<String,Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }

}