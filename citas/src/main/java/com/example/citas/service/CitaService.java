package com.example.citas.service;

import com.example.citas.dto.CitaRequest;
import com.example.citas.dto.CitaResponse;
import com.example.commons.service.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {

    void actualizarEstadoCita(Long idCita, Long idEstadoCita);
}
