package com.inventorsoft.websocket.demo.c_streaming;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SpringBootApplication(
        scanBasePackages = {
        "com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.c_streaming"
        },
        proxyBeanMethods = false
)
public class StreamingController {

    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    GoogleApi googleApi = new GoogleApi();

    @GetMapping(value = "/users-stream")
    public SseEmitter getUsers() {
        SseEmitter sseEmitter = new SseEmitter(Duration.ofHours(NumberUtils.LONG_ONE).toMillis());

        threadPoolTaskExecutor.execute(() -> {
            try {
                for (UserDto user : googleApi.getUsers()) {
                    sseEmitter.send(generateBlockingEvent(user, "sse"));
                    TimeUnit.MILLISECONDS.sleep(700L);
                }
            } catch (IOException | InterruptedException e) {
                log.error("Error", e);
                sseEmitter.completeWithError(e);
            } finally {
                log.debug("Emitter has finished its work");
                sseEmitter.complete();
            }
        });

        return sseEmitter;
    }

    @GetMapping(value = "/users-stream/reactive")
    public Flux<ServerSentEvent<String>> getUsersReactive() {
        return Flux
                .fromIterable(googleApi.getUsers())
                .delayElements(Duration.ofMillis(700L))
                .map(dto -> generateReactiveEvent(dto, "sse-reactive"))
                .doOnComplete(() -> log.debug("Reactive emitter has finished its work"));
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private class UserDto {

        String name;
        Integer id;

    }

    private class GoogleApi {

        @SneakyThrows
        List<UserDto> getUsers() {
            return Stream
                    .iterate(NumberUtils.INTEGER_ZERO, i -> i < 10, i -> i + NumberUtils.INTEGER_ONE)
                    .map(i -> new UserDto("Vova-" + RandomStringUtils.randomAlphanumeric(3) + "-" + i + " ", i))
                    .toList();
        }

    }

    private ServerSentEvent<String> generateReactiveEvent(UserDto userDto, String eventType) {
        return ServerSentEvent
                .builder(userDto.getName())
                .id(userDto.getId().toString())
                .event(eventType)
                .build();
    }

    private SseEmitter.SseEventBuilder generateBlockingEvent(UserDto user, String eventType) {
        return SseEmitter.event()
                .data(user.getName())
                .id(user.getId().toString())
                .name(eventType);
    }

    public static void main(String[] args) {
        SpringApplication.run(StreamingController.class, args);
    }

}
