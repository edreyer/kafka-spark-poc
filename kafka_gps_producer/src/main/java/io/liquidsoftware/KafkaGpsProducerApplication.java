package io.liquidsoftware;

import io.liquidsoftware.domain.service.OpenSkyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class KafkaGpsProducerApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(KafkaGpsProducerApplication.class, args);

        OpenSkyService openSkyService = ctx.getBean(OpenSkyService.class);
        openSkyService.streamGpsEventsToKafka();
    }
}
