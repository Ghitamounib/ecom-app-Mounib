package org.example.kafkaspringcloudstream.handlers;

import org.apache.kafka.common.metrics.stats.Avg;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.example.kafkaspringcloudstream.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class PageEventHandler {
    @Bean
    public Consumer<PageEvent> pageEventConsumer() {
        return (input) -> {
            System.out.println("************");
            System.out.println(input.toString());
            System.out.println("************");
        };
    }

    @Bean
    public Supplier<PageEvent> pageEventSupplier() {
        return () -> {
            return new PageEvent(
                    Math.random() > 0.5 ? "P1" : "P2",
                    Math.random() > 0.5 ? "U1" : "U2",
                    new Date(),
                    10 + new Random().nextInt(10000)
            );
        };
    }
    @Bean
    public Function<KStream<String, PageEvent>, KStream<String, Long>> kStream() {
        return (stream) ->
                stream
                        .map((k, v) -> new KeyValue<>(v.getName(), v.getDuration()))
                        .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(5)))
                        .aggregate(
                                () -> 0L,
                                (key, value, total) -> total + value,
                                Materialized.as("count-store")   // ⚠️ LE STORE DOIT ÊTRE ICI
                        )
                        .toStream()
                        .map((k, v) -> new KeyValue<>(k.key(), v));
    }

}