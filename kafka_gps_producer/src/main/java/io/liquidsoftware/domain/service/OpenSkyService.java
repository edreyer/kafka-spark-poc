package io.liquidsoftware.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.liquidsoftware.domain.model.FlightEvent;
import io.liquidsoftware.domain.model.OpenSkyResponse;
import javaslang.control.Try;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.Sender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by erikdreyer on 3/7/17.
 *
 * https://opensky-network.org/
 *
 */
@Service
public class OpenSkyService {

    private static final Logger LOG = LoggerFactory.getLogger(OpenSkyService.class);

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Sender<Integer, String> sender;

    @Value("${opensky.username}")
    private String username;

    @Value("${opensky.password}")
    private String password;

    @Value("${kafka.topic}")
    private String topic;

    private String openSkyUrl;

    private RestTemplate rest;

    private AtomicInteger counter = new AtomicInteger();

    private SimpleDateFormat dateFormat;

    @PostConstruct
    public void init() {
        openSkyUrl = String.format("https://%s:%s@opensky-network.org/api/states/all", username, password);
        rest = new RestTemplate();
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    public void streamGpsEventsToKafka() {
        Flux<String> flightEventFlux = streamGpsData(10)
            .map(flightEvent -> Try.of(() -> mapper.writeValueAsString(flightEvent)))
            .filter(Try::isSuccess)
            .map(Try::get);

        Flux<SenderRecord<Integer, String, Integer>> senderRecordFlux = flightEventFlux.map(str -> {
            int i = counter.incrementAndGet();
            return SenderRecord.create(new ProducerRecord<>(topic, i, str), i);
        });

        sender.send(senderRecordFlux, true)
            .doOnError(e-> LOG.error("Send failed", e))
            .subscribe(r -> {
                RecordMetadata metadata = r.recordMetadata();
                System.out.printf("Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                    r.correlationMetadata(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    dateFormat.format(new Date(metadata.timestamp())));
            });

    }

    public Flux<FlightEvent> streamGpsData(int intervalSeconds) {
        Flux<FlightEvent> flux = Mono.fromCallable(() -> rest.getForObject(openSkyUrl, OpenSkyResponse.class))
            .subscribeOn(Schedulers.elastic())
            .delaySubscription(Duration.ofSeconds(intervalSeconds))
            .flatMapIterable(openSkyResponse -> openSkyResponse.getStates())
            .filter(event -> Boolean.FALSE == event.get(8)) // not on the ground
            .filter(event -> event.get(5) != null && event.get(6) != null) // has lat and lon
            .map(event -> fromEventObject(event));

        return (intervalSeconds > 0) ? flux.repeat() : flux;
    }

    private FlightEvent fromEventObject(java.util.List<Object> event) {
        return FlightEvent.builder()
            .timestamp(new Date())
            .icao24((String)event.get(0))
            .latitude(Double.valueOf(event.get(6).toString()))
            .longitude(Double.valueOf(event.get(5).toString()))
            .build();
    }

}
