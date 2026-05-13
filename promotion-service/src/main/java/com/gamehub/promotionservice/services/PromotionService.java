package com.gamehub.promotionservice.services;

import com.gamehub.promotionservice.models.Promotion;
import com.gamehub.promotionservice.models.PromotionDTO;
import com.gamehub.promotionservice.repositories.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j // Para manejo de logs profesional
@Service
@RequiredArgsConstructor // Inyección por constructor (Final Fields)
public class PromotionService {

    private final PromotionRepository repository;


    @Transactional
    public Promotion crear(PromotionDTO dto) {
        log.info("Intentando crear promoción con código: {}", dto.getCodigo());

        repository.findByCodigo(dto.getCodigo())
                .ifPresent(p -> {
                    throw new RuntimeException("El código '" + dto.getCodigo() + "' ya está registrado.");
                });

        Promotion promotion = mapToEntity(dto);

        // Reglas de negocio iniciales
        promotion.setUsosActuales(0);
        promotion.setEstado(true);

        return repository.save(promotion);
    }


    @Transactional(readOnly = true)
    public List<Promotion> listar(Boolean soloVigentes) {
        return (soloVigentes != null)
                ? repository.findByEstado(soloVigentes)
                : repository.findAll();
    }

    @Transactional(readOnly = true)
    public Promotion buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("No se encontró la promoción con código: " + codigo));
    }


    @Transactional
    public Promotion actualizar(Long id, PromotionDTO dto) {
        return repository.findById(id)
                .map(existingPromotion -> {
                    existingPromotion.setFechaFin(dto.getFechaFin());
                    existingPromotion.setValor(dto.getValor());
                    existingPromotion.setUsosMaximos(dto.getUsosMaximos());
                    log.info("Actualizando promoción ID: {}", id);
                    return repository.save(existingPromotion);
                })
                .orElseThrow(() -> new RuntimeException("Error: ID " + id + " no encontrado."));
    }

    @Transactional
    public void desactivar(Long id) {
        Promotion promotion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede desactivar: ID " + id + " no existe."));

        promotion.setEstado(false);
        repository.save(promotion);
        log.warn("Promoción ID {} ha sido desactivada.", id);
    }


    private Promotion mapToEntity(PromotionDTO dto) {
        Promotion p = new Promotion();
        p.setCodigo(dto.getCodigo());
        p.setTipo(dto.getTipo());
        p.setValor(dto.getValor());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setMontoMinimo(dto.getMontoMinimo());
        p.setUsosMaximos(dto.getUsosMaximos());
        return p;
    }
}