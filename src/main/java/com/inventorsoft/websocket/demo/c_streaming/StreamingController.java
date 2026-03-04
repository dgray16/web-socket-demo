package com.inventorsoft.websocket.demo.c_streaming;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@RestController
@SpringBootApplication(
        scanBasePackages = {"com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.c_streaming"},
        proxyBeanMethods = false
)
class StreamingController {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingController.class);

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final GoogleApi googleApi = new GoogleApi();

    StreamingController(final ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @GetMapping(value = "/users-stream")
    SseEmitter getUsers() {
        SseEmitter sseEmitter = new SseEmitter(Duration.ofHours(NumberUtils.LONG_ONE).toMillis());

        threadPoolTaskExecutor.execute(() -> {
            try {
                for (UserDto user : googleApi.getUsers()) {
                    sseEmitter.send(generateBlockingEvent(user, "sse"));
                    TimeUnit.MILLISECONDS.sleep(700L);
                }
            } catch (IOException | InterruptedException e) {
                LOG.error("Error", e);
                sseEmitter.completeWithError(e);
            } finally {
                LOG.debug("Emitter has finished its work");
                sseEmitter.complete();
            }
        });

        return sseEmitter;
    }

    @GetMapping(value = "/users-stream/reactive")
    Flux<ServerSentEvent<String>> getUsersReactive() {
        return Flux
                .fromIterable(googleApi.getUsers())
                .delayElements(Duration.ofMillis(700L))
                .map(dto -> generateReactiveEvent(dto, "sse-reactive"))
                .doOnComplete(() -> LOG.debug("Reactive emitter has finished its work"));
    }

    private record UserDto(String name, Integer id) {}

    private static class GoogleApi {
        List<UserDto> getUsers() {
            return Stream
                    .iterate(NumberUtils.INTEGER_ZERO, i -> i < 10, i -> i + NumberUtils.INTEGER_ONE)
                    .map(i -> new UserDto("Vova-" + RandomStringUtils.insecure().nextAlphabetic(3) + "-" + i + " ", i))
                    .toList();
        }

    }

    private ServerSentEvent<String> generateReactiveEvent(UserDto userDto, String eventType) {
        return ServerSentEvent
                .builder(userDto.name())
                .id(userDto.id().toString())
                .event(eventType)
                .build();
    }

    private SseEmitter.SseEventBuilder generateBlockingEvent(UserDto user, String eventType) {
        return SseEmitter
                .event()
                .data(user.name())
                .id(user.id().toString())
                .name(eventType);
    }

    static void main(String[] args) {
        SpringApplication.run(StreamingController.class, args);
    }

}
