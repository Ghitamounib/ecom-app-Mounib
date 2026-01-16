package org.example.kafkaspringcloudstream.controllers;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.example.kafkaspringcloudstream.entities.PageEvent;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class PageEventController {

    private final StreamBridge streamBridge;
    private final InteractiveQueryService interactiveQueryService;

    public PageEventController(StreamBridge streamBridge, InteractiveQueryService interactiveQueryService) {
        this.streamBridge = streamBridge;
        this.interactiveQueryService = interactiveQueryService;
    }

    @GetMapping("publish/{topic}/{name}")
    public PageEvent publish(@PathVariable String name, @PathVariable String topic){
        PageEvent pageEvent = new PageEvent();
        pageEvent.setName(name);
        pageEvent.setDate(new Date());
        pageEvent.setDuration(new Random().nextInt(1000));
        pageEvent.setUser(Math.random() > 0.5 ? "U1" : "U2");
        streamBridge.send(topic, pageEvent);
        return pageEvent;
    }
    @GetMapping(path = "/analytics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> analytics() {

        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> {

                    Map<String, Long> result = new HashMap<>();

                    ReadOnlyWindowStore<String, Long> store =
                            interactiveQueryService.getQueryableStore(
                                    "count-store",
                                    QueryableStoreTypes.windowStore()
                            );

                    Instant now = Instant.now();
                    Instant from = now.minusMillis(5000);

                    try (KeyValueIterator<Windowed<String>, Long> fetchAll =
                                 store.fetchAll(from, now)) {

                        while (fetchAll.hasNext()) {
                            KeyValue<Windowed<String>, Long> record = fetchAll.next();

                            // VERSION LA PLUS COMPATIBLE
                            String key = record.key.key();
                            Long value = record.value;

                            result.put(key, value);
                        }
                    }

                    return result;
                });
    }

    }

