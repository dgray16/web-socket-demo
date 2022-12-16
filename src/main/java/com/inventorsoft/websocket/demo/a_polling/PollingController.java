package com.inventorsoft.websocket.demo.a_polling;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SpringBootApplication(
        scanBasePackages = {
                "com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.a_polling"
        },
        proxyBeanMethods = false
)
public class PollingController {

    PatientWebService patientWebService = new PatientWebService();

    @GetMapping(value = "/patient/profile")
    public ResponseEntity<PatientProfileDto> getPatientProfile() {
        log.debug("Preparing blocking response...");
        return ResponseEntity.ok(patientWebService.getPatientProfile());
    }

    @GetMapping(value = "/patient/profile/reactive")
    public ResponseEntity<Mono<PatientProfileDto>> getPatientProfileReactive() {
        log.debug("Preparing reactive response...");
        return ResponseEntity.ok(patientWebService.getPatientProfileReactive());
    }

    private record PatientProfileDto(String name) {}

    private static class PatientWebService {

        PatientProfileDto getPatientProfile() {
            return new PatientProfileDto("Vova");
        }

        Mono<PatientProfileDto> getPatientProfileReactive() {
            return Mono.just(new PatientProfileDto("Vova"));
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(PollingController.class, args);
    }


}
