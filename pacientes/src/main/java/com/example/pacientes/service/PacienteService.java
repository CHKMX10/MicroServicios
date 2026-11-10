package com.example.pacientes.service;

import com.example.commons.dto.medicos.MedicoResponse;
import com.example.commons.dto.pacientes.PacienteReponse;
import com.example.commons.dto.pacientes.PacienteRequest;
import com.example.commons.service.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteReponse> {

    PacienteReponse obtenerPacientePorIdSinEstado(Long id);


}
