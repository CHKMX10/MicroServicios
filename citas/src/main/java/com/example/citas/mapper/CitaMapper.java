package com.example.citas.mapper;

import com.example.citas.dto.CitaRequest;
import com.example.citas.dto.CitaResponse;
import com.example.citas.entity.Cita;
import com.example.commons.dto.DatosMedico;
import com.example.commons.dto.DatosPaciente;
import com.example.commons.dto.medicos.MedicoResponse;
import com.example.commons.dto.pacientes.PacienteReponse;
import com.example.commons.dto.pacientes.PacienteRequest;
import com.example.commons.mapper.CommonMapper;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {

    @Override
    public Cita requestAEntidad(CitaRequest request) {
        return request == null
                ? null
                : Cita.crear(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        return entidad == null
                ? null
                : new CitaResponse(
                        entidad.getId(),
                null,
                null,
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion()
        );
    }

    private DatosPaciente pacienteResponseADatosPaciete(PacienteReponse paciente){
        return paciente == null
                ? null
                : new DatosPaciente(
                        paciente.nombre(),
                        paciente.numExpediendte(),
                        paciente.edad() + "Años",
                        paciente.peso() + "Kg",
                        paciente.estatura() + "m",
                        String.join(" ",
                                    Math.round(paciente.imc() * 100.0) / 100 + "",
                                    clasificacionIMC(paciente.imc())),
                paciente.telefono()
        );

        }

    private String clasificacionIMC(double imc) {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25) return "Peso normal";
        if (imc < 30) return "Sobrepeso";
        if (imc < 35) return "Obesidad grado 1";
        if (imc < 40) return "Obesidad grado 2";
        return "Obesidad grado 3";
    }

    private DatosMedico medicoResponseADatosMedicos(MedicoResponse medico){

        return medico == null
                ? null
                : new DatosMedico(
                        medico.nombre(),
                        medico.cedulaProfesioonal(),
                        medico.especialidad());
    }
}


