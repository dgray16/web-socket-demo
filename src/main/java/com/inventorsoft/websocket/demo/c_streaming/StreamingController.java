package com.inventorsoft.websocket.demo.c_streaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = {"com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.c_streaming"})
@RestController
@AllArgsConstructor
@Slf4j
public class StreamingController {

    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @GetMapping(value = "/users-stream")
    public ResponseBodyEmitter getUsers() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(1000000000L);

        List<UserDTO> users = new GoogleAPI().getUsers();

        threadPoolTaskExecutor.execute(() -> {
            try {
                for (UserDTO user : users) {
                    emitter.send(user.getName(), MediaType.TEXT_HTML);
                    TimeUnit.MILLISECONDS.sleep(200L);
                }
            } catch(Exception e) {
                emitter.completeWithError(e);
            } finally {
                log.debug("Emitter has been finished");
                emitter.complete();
            }
        });

        return emitter;
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

            for (int i = 0; i < 300; i++) {
                users.add(new UserDTO(RandomStringUtils.randomAlphanumeric(10) + "-" + i + " "));
            }

            return users;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(StreamingController.class, args);
    }
}
