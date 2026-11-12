package com.example.commons.clients;

import com.example.commons.dto.pacientes.PacienteReponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacientes")
public interface PacienteClient {

    @GetMapping("/{id}")
    PacienteReponse obtenerPacienteActivoPorId(@PathVariable("id") Long id);

    @GetMapping("/id-paciente/{id}")
    PacienteReponse obtenerPacienteSinEstadoPorId(@PathVariable("id") Long id);

}
