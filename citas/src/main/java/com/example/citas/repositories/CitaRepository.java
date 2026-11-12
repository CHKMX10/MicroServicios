package com.example.citas.repositories;

import com.example.citas.entity.Cita;
import com.example.citas.enums.EstadoCita;
import com.example.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro (EstadoRegistro estadoRegistro);

    //consulta sql
    //SELECT COUNT(*) FROM CITAS WHERE ID_MEDICO = ? AND
    //          ESTADO_REGISTRO = ? AND ESTADO_CITA IN(?);
    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estadoCitas
    );

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estadosCitas
    );

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idMedico,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estadoCitas,
            Long id
    );

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idPaciente,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estadoCitas, Long id
    );





}
