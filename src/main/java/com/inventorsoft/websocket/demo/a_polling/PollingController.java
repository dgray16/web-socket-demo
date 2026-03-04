package com.inventorsoft.websocket.demo.a_polling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication(
        scanBasePackages = {
                "com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.a_polling"
        },
        proxyBeanMethods = false
)
class PollingController {

    private static final Logger LOG = LoggerFactory.getLogger(PollingController.class);

    private final PatientWebService patientWebService = new PatientWebService();

    @GetMapping(value = "/patient/profile")
    ResponseEntity<PatientProfileDto> getPatientProfile() {
        LOG.debug("Preparing blocking response...");
        return ResponseEntity.ok(patientWebService.getPatientProfile());
    }

    @GetMapping(value = "/patient/profile/reactive")
    ResponseEntity<Mono<PatientProfileDto>> getPatientProfileReactive() {
        LOG.debug("Preparing reactive response...");
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

    static void main(String[] args) {
        SpringApplication.run(PollingController.class, args);
    }


}
