package com.inventorsoft.websocket.demo.b_long_polling;

import com.inventorsoft.websocket.demo.a_polling.PollingController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = {"com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.b_long_polling"})
@RestController
@AllArgsConstructor
public class LongPollingController {

    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @GetMapping(value = "/users")
    public DeferredResult<ResponseEntity<List<UserDTO>>> getUsers() {
        DeferredResult<ResponseEntity<List<UserDTO>>> result = new DeferredResult<>();

        threadPoolTaskExecutor.execute(() -> {
            List<UserDTO> users = new GoogleAPI().getUsers();
            result.setResult(ResponseEntity.ok(users));
        });

        return result;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private class UserDTO {
        private String name;
    }

    private class GoogleAPI {

        @SneakyThrows
        List<UserDTO> getUsers() {
            TimeUnit.SECONDS.sleep(3L);
            List<UserDTO> users = new ArrayList<>();

            users.add(new UserDTO("Vova"));
            users.add(new UserDTO("Antony"));
            users.add(new UserDTO("Andrew"));
            users.add(new UserDTO("Mykola"));
            users.add(new UserDTO("Anna Test"));
            users.add(new UserDTO("Anna Front"));

            return users;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(PollingController.class, args);
    }
}
