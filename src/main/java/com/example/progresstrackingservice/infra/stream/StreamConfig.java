package com.example.progresstrackingservice.infra.stream;

import com.example.progresstrackingservice.domain.model.ProgressEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.mail.MailParseException;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class StreamConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs(){
        Map<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);

        return new KafkaStreamsConfiguration(props);
    }
    @Bean
    public void kStream(StreamsBuilder builder, ObjectMapper mapper) {
        // 1️⃣ Kafka Source
        KStream<String, ProgressEvent> events = builder
                .stream("user-event-raw", Consumed.with(Serdes.String(), Serdes.String()))
                .mapValues(value -> {
                    try {
                        return mapper.readValue(value, ProgressEvent.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter((k, v) -> v != null && v.getStep() != null && v.getStatus() != null);

        Serde<ProgressEvent> progressSerde = new JsonSerde<>(ProgressEvent.class);

        KTable<String, Long> totalCounts = events
                .groupBy((k, v) -> v.getStep().getName(),
                        Grouped.with(Serdes.String(), progressSerde))
                .count(Materialized.as("total-counts"));

        KTable<String, Long> successCounts = events
                .filter((k, v) -> "SUCCESS".equals(v.getStatus()))
                .groupBy((k, v) -> v.getStep().getName(),
                        Grouped.with(Serdes.String(), progressSerde))
                .count(Materialized.as("success-counts"));

        KTable<String, Long> failCounts = events
                .filter((k, v) -> "FAIL".equals(v.getStatus()))
                .groupBy((k, v) -> v.getStep().getName(),
                        Grouped.with(Serdes.String(), progressSerde))
                .count(Materialized.as("fail-counts"));

        KTable<String, StepStatus> joined = totalCounts.join(
                successCounts,
                (total, success) -> new StepStatus(total, success, 0L)
        );

        KTable<String, StepStatus> fullStats = joined.leftJoin(
                failCounts,
                (stats, fail) -> {
                    if (stats == null) return new StepStatus(0L, 0L, fail == null ? 0L : fail);
                    stats.setFailCount(fail == null ? 0L : fail);
                    stats.recalculate();
                    return stats;
                }
        );

        // 7️⃣ Kafka로 출력
        fullStats.toStream()
                .mapValues(stats -> {
                    try {
                        return mapper.writeValueAsString(stats);
                    } catch (JsonProcessingException e) {
                        return "{}";
                    }
                })
                .to("user-event-agg-stat", Produced.with(Serdes.String(), Serdes.String()));
    }

}
