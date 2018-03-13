package com.wordchain.repository;

import com.wordchain.model.Game;
import com.wordchain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface GameRepository extends JpaRepository<Game, Long> {

    Game findById(Long gameId);

    @Override
    @Transactional
    void delete(Game game);

    /*@Query("UPDATE Game g SET g.players = :players WHERE g.id = :gameId")
    @Modifying
    @Transactional
    void addPlayerToGame(@Param("gameId") long gameId, @Param("players") List<Player> players);*/

    /*@Transactional
    @Modifying
    @Query("UPDATE Game g SET g.players = ?1 WHERE g.id = ?2")
    void addPlayerToGame(List<Player> players, Long id);*/

    /*@Query(value = "INSERT INTO game_players VALUES (?, ?);",
          nativeQuery = true)
    @Modifying
    @Transactional
    void addPlayerToGame(Long gameId, Long playerId);*/

    /*@Query(value = "DELETE FROM game_players " +
            "WHERE games_id = ? AND players_id = ?;",
            nativeQuery = true)
    @Modifying
    @Transactional
    void deletePlayerFromGame(Long gameId, Long playerId);*/



    /*@Transactional
    @Modifying
    @Query("DELETE FROM Game g WHERE g.id = :gameId")
    void deleteGameById(@Param("gameId") long gameId);*/

    /*@Query(value = "UPDATE game SET status = ? WHERE id = ?;",
            nativeQuery = true)
    @Modifying
    @Transactional
    void changeGameStatus(String status, Long gameId);*/

    /*@Query(value = "UPDATE game SET word_chain = ? WHERE id = ?;",
            nativeQuery = true)
    @Modifying
    @Transactional
    void updateWordChain(String chain, Long gameId);*/

}
