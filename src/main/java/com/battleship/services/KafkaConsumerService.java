package com.battleship.services;

import com.battleship.models.GameModelUI;
import com.battleship.models.SavingGame;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topic.games}")
    private String games;
    @Value("${spring.kafka.topic.game-models}")
    private String gameModels;

    public List<SavingGame> getSavedGamesFromKafka() {
        LOGGER.debug("getSavedGamesFromKafka() started");
        List<SavingGame> savingGames = new ArrayList<>();

        KafkaConsumer<String, SavingGame> consumer = new KafkaConsumer<>(
                getProps(), new StringDeserializer(), new JsonDeserializer<>(SavingGame.class));

        consumer.subscribe(Arrays.asList(games));
        ConsumerRecords<String, SavingGame> records = consumer.poll(Duration.ofMillis(1500));
        for (ConsumerRecord<String, SavingGame> record : records) {
            savingGames.add(record.value());
        }
        consumer.close();
        return savingGames;
    }

    public List<GameModelUI> getGameModelUIsFromKafka(UUID gameId) {
        LOGGER.debug("getGameModelUIsFromKafka("+gameId+") started");
        List<GameModelUI> gameModelUIList = new ArrayList<>();

        KafkaConsumer<String, GameModelUI> consumer = new KafkaConsumer<>(
                getProps(), new StringDeserializer(), new JsonDeserializer<>(GameModelUI.class));
        consumer.subscribe(Arrays.asList(gameModels));
        ConsumerRecords<String, GameModelUI> records = consumer.poll(Duration.ofMillis(2000));
        for (ConsumerRecord<String, GameModelUI> record : records) {
            GameModelUI gameModelUI = record.value();
            if(gameModelUI.getGameId().equals(gameId)) {
                gameModelUIList.add(gameModelUI);
            }
        }
        consumer.close();
        return gameModelUIList;
    }

    private Map<String, Object> getProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
