package com.gamehub.userservice.services;

import com.gamehub.userservice.models.Direccion;
import com.gamehub.userservice.models.Dtos.DireccionRequestDTO;
import com.gamehub.userservice.models.Usuario;
import com.gamehub.userservice.repositories.DireccionRepository;
import com.gamehub.userservice.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public Direccion crearDireccion(DireccionRequestDTO dto) {
        log.info("Agregando dirección para el usuario ID: {}", dto.getUsuarioId());

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Direccion direccion = new Direccion();
        direccion.setComuna(dto.getComuna());
        direccion.setCiudad(dto.getCiudad());
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setUsuario(usuario);

        return direccionRepository.save(direccion);
    }

    public List<Direccion> listarPorUsuario(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId);
    }

    public Direccion actualizarDireccion(Long id, DireccionRequestDTO dto) {
        log.info("Actualizando dirección ID: {}", id);
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada"));

        direccion.setComuna(dto.getComuna());
        direccion.setCiudad(dto.getCiudad());
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());

        return direccionRepository.save(direccion);
    }

    public void eliminarDireccion(Long id) {
        log.info("Eliminando dirección ID: {}", id);
        if (!direccionRepository.existsById(id)) {
            throw new IllegalArgumentException("La dirección no existe");
        }
        direccionRepository.deleteById(id);
    }
}