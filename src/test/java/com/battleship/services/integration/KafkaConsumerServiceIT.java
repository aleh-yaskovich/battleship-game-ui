package com.battleship.services.integration;

import com.battleship.models.GameModelUI;
import com.battleship.models.PlayerModelUI;
import com.battleship.models.SavingGame;
import com.battleship.services.KafkaConsumerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ContextConfiguration
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaConsumerServiceIT {

    @Value("${spring.kafka.topic.games}")
    private String games;
    @Value("${spring.kafka.topic.game-models}")
    private String gameModels;
    @Autowired
    private KafkaTemplate<String, SavingGame> kafkaTemplateForGames;
    @Autowired
    private KafkaTemplate<String, GameModelUI> kafkaTemplateForGameModels;
    @Autowired
    private KafkaConsumerService service;

    @Test
    void getSavedGamesFromKafkaTest() {
        SavingGame savingGame = new SavingGame("A vs B", UUID.randomUUID());
        kafkaTemplateForGames.send(games, savingGame);
        List<SavingGame> actual = service.getSavedGamesFromKafka();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(savingGame, actual.get(0));
    }

    @Test
    void getGameModelUIsFromKafkaTest() {
        UUID gameId = UUID.randomUUID();
        List<GameModelUI> gameModelUIList = List.of(
                new GameModelUI(gameId, new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID()),
                new GameModelUI(gameId, new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID()),
                new GameModelUI(UUID.randomUUID(), new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID())
        );
        for(GameModelUI gm : gameModelUIList) {
            kafkaTemplateForGameModels.send(gameModels, gm);
        }
        List<GameModelUI> actual = service.getGameModelUIsFromKafka(gameId);
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(gameId, actual.get(0).getGameId());
        assertEquals(gameId, actual.get(1).getGameId());
    }
}
