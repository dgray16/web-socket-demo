package com.inventorsoft.websocket.demo.b_long_polling;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SpringBootApplication(
        scanBasePackages = {
                "com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.b_long_polling"
        },
        proxyBeanMethods = false
)
class LongPollingController {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingController.class);

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final GoogleApi googleApi = new GoogleApi();

    LongPollingController(final ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @GetMapping(value = "/users")
    DeferredResult<ResponseEntity<List<UserDto>>> getUsers() {
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
    DeferredResult<ResponseEntity<Flux<UserDto>>> getUsersReactive() {
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

        List<UserDto> getUsers() {
            try {
                TimeUnit.SECONDS.sleep(4L);
            } catch (InterruptedException e) {}

            LOG.debug("Get users call...");
            return getUsersStream().toList();
        }

        Flux<UserDto> getUsersReactive() {
            try {
                TimeUnit.SECONDS.sleep(4L);
            } catch (InterruptedException e) {}
            LOG.debug("Get users call reactive...");
            return Flux.fromStream(this::getUsersStream);
        }

        private Stream<UserDto> getUsersStream() {
            return IntStream
                    .range(NumberUtils.INTEGER_ZERO, 5)
                    .mapToObj(i -> "Vova-" + RandomStringUtils.insecure().nextAlphabetic(i + NumberUtils.INTEGER_ONE))
                    .map(UserDto::new);
        }

    }

    static void main(String[] args) {
        SpringApplication.run(LongPollingController.class, args);
    }

}
