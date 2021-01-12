// samples for the usage of keywords inside method names:
// https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html

package com.wordchain.repository;

import com.wordchain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findById(Long playerId);

    // Player findByEmail(String email);
    
    Player findByUserName(String userName);

}
