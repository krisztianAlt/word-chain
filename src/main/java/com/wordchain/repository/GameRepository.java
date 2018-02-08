package com.wordchain.repository;

import com.wordchain.model.Game;
import com.wordchain.model.Player;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import javax.persistence.NamedQuery;
import javax.transaction.Transactional;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    /*void updatePlayerListOfGame(@Param("players") List<Player> players,
                                @Param("gameId") Long gameId);*/

    /*@Query(value = "UPDATE Game g SET g.players = ?1 WHERE g.id = ?2",
    nativeQuery = false)
    @Modifying
    @Transactional
    void updatePlayerListOfGame(List<Player> players, Long id);*/

    @Query(value = "INSERT INTO game_players VALUES (?, ?);",
          nativeQuery = true)
    @Modifying
    @Transactional
    void addPlayerToGame(Long gameId, Long playerId);

    @Query(value = "DELETE FROM game_players " +
            "WHERE games_id = ? AND players_id = ?;",
            nativeQuery = true)
    @Modifying
    @Transactional
    void deletePlayerFromGame(Long gameId, Long playerId);

    Game getGameById(@Param("gameId") long gameId);

}
