package com.rica.api.investigadores_service.investigadores.dominio;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "investigadores")
public class Investigador {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Embedded
    @AttributeOverride(name = "valor",
            column = @Column(name = "correo_institucional", nullable = false, unique = true, length = 150))
    private CorreoInstitucional correoInstitucional;

    @Column(name = "grupo_investigacion", length = 150)
    private String grupoDeInvestigacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInvestigador estado = EstadoInvestigador.ACTIVO;

    public Investigador() {
    }

    public Investigador(Long id, String nombreCompleto, CorreoInstitucional correoInstitucional, String grupoDeInvestigacion) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.correoInstitucional = correoInstitucional;
        this.grupoDeInvestigacion = grupoDeInvestigacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public CorreoInstitucional getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(CorreoInstitucional correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getGrupoDeInvestigacion() {
        return grupoDeInvestigacion;
    }

    public void setGrupoDeInvestigacion(String grupoDeInvestigacion) {
        this.grupoDeInvestigacion = grupoDeInvestigacion;
    }

    public EstadoInvestigador getEstado() {
        return estado;
    }

    public boolean estaEnEliminacion() {
        return estado == EstadoInvestigador.EN_ELIMINACION;
    }

    public void marcarEnEliminacion() {
        this.estado = EstadoInvestigador.EN_ELIMINACION;
    }
}
