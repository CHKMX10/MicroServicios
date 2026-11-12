package com.example.auth.service;

import com.example.auth.dto.UsuarioRequest;
import com.example.auth.dto.UsuarioResponse;
import com.example.auth.dto.UsuarioUpdateRequest;

import java.util.Set;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse actualizar(String username, UsuarioUpdateRequest request);

    void eliminar(String username);
}
