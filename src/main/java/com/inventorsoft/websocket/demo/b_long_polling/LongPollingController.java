package com.inventorsoft.websocket.demo.b_long_polling;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SpringBootApplication(
        scanBasePackages = {
                "com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.b_long_polling"
        },
        proxyBeanMethods = false
)
@Slf4j
public class LongPollingController {

    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    GoogleApi googleApi = new GoogleApi();

    @GetMapping(value = "/users")
    public DeferredResult<ResponseEntity<List<UserDto>>> getUsers() {
        DeferredResult<ResponseEntity<List<UserDto>>> result = new DeferredResult<>();

        threadPoolTaskExecutor.execute(() -> {
            List<UserDto> users = new ArrayList<>();

            for (int i = NumberUtils.INTEGER_ZERO; i < NumberUtils.INTEGER_TWO; i++) {
                users.addAll(googleApi.getUsers());
            }

            result.setResult(ResponseEntity.ok(users));
        });

        return result;
    }

    @GetMapping(value = "/users/reactive")
    public DeferredResult<ResponseEntity<Flux<UserDto>>> getUsersReactive() {
        DeferredResult<ResponseEntity<Flux<UserDto>>> result = new DeferredResult<>();

        threadPoolTaskExecutor.execute(() -> {
            Flux<UserDto> usersFlux = Flux.empty();

            for (int i = NumberUtils.INTEGER_ZERO; i < NumberUtils.INTEGER_TWO; i++) {
                usersFlux = usersFlux.mergeWith(googleApi.getUsersReactive());
            }

            result.setResult(ResponseEntity.ok(usersFlux));
        });

        return result;
    }

    private record UserDto(String name) {}

    private static class GoogleApi {

        @SneakyThrows
        List<UserDto> getUsers() {
            TimeUnit.SECONDS.sleep(4L);
            log.debug("Get users call...");
            return getUsersStream().toList();
        }

        @SneakyThrows
        Flux<UserDto> getUsersReactive() {
            TimeUnit.SECONDS.sleep(4L);
            log.debug("Get users call reactive...");
            return Flux.fromStream(this::getUsersStream);
        }

        private Stream<UserDto> getUsersStream() {
            return IntStream
                    .range(NumberUtils.INTEGER_ZERO, 5)
                    .mapToObj(i -> "Vova-" + RandomStringUtils.randomAlphabetic(i + NumberUtils.INTEGER_ONE))
                    .map(UserDto::new);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(LongPollingController.class, args);
    }

}
