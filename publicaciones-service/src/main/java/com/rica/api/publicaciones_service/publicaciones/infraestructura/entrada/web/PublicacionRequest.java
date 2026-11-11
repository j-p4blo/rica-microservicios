package com.rica.api.publicaciones_service.publicaciones.infraestructura.entrada.web;

import java.util.Map;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PublicacionRequest {

	@NotBlank(message = "El correo del investigador es obligatorio")
	@Email(message = "El correo del investigador debe tener un formato válido")
	private String investigadorCorreo;

	@NotBlank(message = "El título es obligatorio")
	private String titulo;

	@NotBlank(message = "El tipo de publicación es obligatorio")
	private String tipo;

	@NotNull(message = "El año de publicación es obligatorio")
	private Integer anio;

	private Map<String, String> detalles;

	public PublicacionRequest() {
	}

	public String getInvestigadorCorreo() {
		return investigadorCorreo;
	}

	public void setInvestigadorCorreo(String investigadorCorreo) {
		this.investigadorCorreo = investigadorCorreo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	public Map<String, String> getDetalles() {
		return detalles;
	}

	public void setDetalles(Map<String, String> detalles) {
		this.detalles = detalles;
	}
}
