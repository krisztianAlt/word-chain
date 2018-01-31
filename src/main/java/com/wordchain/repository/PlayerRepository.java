package com.wordchain.repository;

import com.wordchain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player getPlayerByEmail(@Param("email") String email);

    Player getPlayerById(@Param("id") Long id);

}
