package com.example.medicos.service;

import com.example.commons.dto.medicos.MedicoRequest;
import com.example.commons.dto.medicos.MedicoResponse;
import com.example.commons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);


}
