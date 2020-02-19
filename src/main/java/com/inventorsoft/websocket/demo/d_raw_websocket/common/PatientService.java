package com.inventorsoft.websocket.demo.d_raw_websocket.common;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class PatientService {

    public List<Patient> findAll() {
        return List.of(new Patient("Vova"), new Patient("Antony"));
    }

    public Flux<Patient> findAllReactive() {
        return Flux.fromIterable(findAll());
    }

}
