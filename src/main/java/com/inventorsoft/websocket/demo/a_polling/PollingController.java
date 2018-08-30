package com.inventorsoft.websocket.demo.a_polling;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.a_polling"})
@RestController
public class PollingController {

    private PatientWebService patientWebService = new PatientWebService();

    @GetMapping(value = "/patient/profile")
    public ResponseEntity<PatientProfileDTO> getPatientProfile() {
        return ResponseEntity.ok(patientWebService.getPatientProfile());
    }

    @NoArgsConstructor
    @Data
    private class PatientProfileDTO {
        private String name = "Vova";
    }

    private class PatientWebService {

        PatientProfileDTO getPatientProfile() {
            return new PatientProfileDTO();
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(PollingController.class, args);
    }


}
