package com.example.citas.service;


import com.example.citas.dto.CitaRequest;
import com.example.citas.dto.CitaResponse;
import com.example.citas.entity.Cita;
import com.example.citas.enums.EstadoCita;
import com.example.citas.mapper.CitaMapper;
import com.example.commons.clients.MedicoClient;
import com.example.citas.repositories.CitaRepository;
import com.example.commons.clients.PacienteClient;
import com.example.commons.dto.medicos.MedicoResponse;
import com.example.commons.dto.pacientes.PacienteReponse;
import com.example.commons.enums.DisponibilidadMedico;
import com.example.commons.enums.EstadoRegistro;
import com.example.commons.exceptions.ConflictoException;
import com.example.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;

    private final PacienteClient pacienteClient;

    private static final List<EstadoCita> ESTADOS_ACTIVOS_INVALIDOS =
            List.of(EstadoCita.CONFIRMADA,
                    EstadoCita.EN_CURSO);

    private static final List<EstadoCita> ESTADOS_PACIENTE_OCUPADO =
            List.of(
                    EstadoCita.PENDIENTE,
                    EstadoCita.CONFIRMADA,
                    EstadoCita.EN_CURSO
            );
    private static final List<EstadoCita> ESTADOS_IMPIDEN_ELIMINAR_MEDICO =
            List.of(
                    EstadoCita.PENDIENTE,
                    EstadoCita.CONFIRMADA,
                    EstadoCita.EN_CURSO
            );

    @Override
    @Transactional(readOnly = true)
    public void medicoTieneCitasAsignadas(Long idMedico) {
        log.info("Validando si el medico tiene cita activa con los estados: {}",
                ESTADOS_ACTIVOS_INVALIDOS);

        if (citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS
        ))
            throw new ConflictoException("No se puede modificar o eliminar la cita ya que " +
                    "tiene citas en estado: " + ESTADOS_ACTIVOS_INVALIDOS);
    }

    @Override
    public void pacienteTieneCitas(Long idPaciente) {
        log.info("Validando si el medico tiene cita activa con los estados: {}",
                ESTADOS_ACTIVOS_INVALIDOS);

        if (citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS
        ))
            throw new ConflictoException("No se puede modificar o eliminar la cita ya que " +
                    "tiene citas en estado: " + ESTADOS_ACTIVOS_INVALIDOS);
    }

    @Override
    public List<CitaResponse> listar() {

        log.info("Listando todas las citas activas...");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        pacienteClient.obtenerPacienteSinEstadoPorId(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = obtenerCitaOException(id);

        return citaMapper.entidadAResponse(
                cita,
                pacienteClient.obtenerPacienteSinEstadoPorId(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva Cita");

        PacienteReponse paciente = pacienteClient.obtenerPacienteActivoPorId(request.idPaciente());

        validarPacienteSinCitasActivas(request.idPaciente(), null);

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        validarMedicoActivoDisponible(medico);

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada");

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = obtenerCitaOException(id);

        if (cita.getEstadoCita() == EstadoCita.EN_CURSO)
            throw new ConflictoException("La cita está en curso y no puede actualizarse");

        PacienteReponse paciente = pacienteClient.obtenerPacienteActivoPorId(request.idPaciente());

        if (!request.idPaciente().equals(cita.getIdPaciente())) {
            validarPacienteSinCitasActivas(request.idPaciente(), id);
        }

        boolean cambiaMedico = !request.idMedico().equals(cita.getIdMedico());

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        if (cambiaMedico) validarMedicoActivoDisponible(medico);

        Long idMedicoAntes = cita.getIdMedico();

        log.info("Actualizando Cita con ID: {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        citaRepository.saveAndFlush(cita);

        log.info("Cita actualizada");

        if (cambiaMedico) {
            actualizarDisponibilidadMedico(idMedicoAntes, DisponibilidadMedico.DISPONIBLE.getCodigo());
            cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());
        }

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }


    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        Cita cita = obtenerCitaOException(idCita);

        log.info("Actualizar el estado de la cita con ID: {}", idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita)
        );

        citaRepository.saveAndFlush(cita);

        log.info("Estado de la cita {} actualizado", idCita);
        cambiarDisponibilidadMedicoSegunEstadoCita(
                cita.getIdMedico(),
                cita.getEstadoCita()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public void validarEliminacionMedico(Long idMedico) {
        if (citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico, EstadoRegistro.ACTIVO, ESTADOS_IMPIDEN_ELIMINAR_MEDICO)) {
            throw new ConflictoException(
                    "No se puede eliminar el médico: tiene citas PENDIENTES, CONFIRMADAS o EN_CURSO");
        }
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con ID: {}", id);

        cita.eliminar();

        citaRepository.saveAndFlush(cita);

        log.info("Cita ha sido marcada como eliminada con ID: {}", id);

        if (cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo());
    }

    public Cita obtenerCitaOException(Long id) {

        log.info("Buscando cita con ID: {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con ID: " + id));
    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        log.info("Actualizando disponibilidad del medico en el servicio remoto");

        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        log.info("Disponibilidad del medico actualizada en el servicio remoto");
    }

    public void validarMedicoActivoDisponible(MedicoResponse medico) {
        log.info("Validando Medico activo.....");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictoException("El medico activo no esta disponible para consultar");
    }

    private void validarPacienteSinCitasActivas(Long idPaciente, Long idCitaExcluida) {

        boolean ocupado = idCitaExcluida == null

                ? citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente, EstadoRegistro.ACTIVO, ESTADOS_PACIENTE_OCUPADO)

                : citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
                idPaciente, EstadoRegistro.ACTIVO, ESTADOS_PACIENTE_OCUPADO, idCitaExcluida);

        if (ocupado)
            throw new ConflictoException("El paciente ya tiene una cita activa pendiente, confirmada o en curso");
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {

        log.info("Buscando médico activo con id {} en el servicio remoto", id);

        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {

        log.info("Buscando médico con id {} en el servicio remoto", id);
        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {

        switch (estadoCita) {

            case PENDIENTE, CONFIRMADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());

            case FINALIZADA, CANCELADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());

            case EN_CURSO -> actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
        }
    }

}

