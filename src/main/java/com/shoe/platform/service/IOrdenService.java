package com.shoe.platform.service;

import java.util.List;
import java.util.Optional;

import com.shoe.platform.model.Orden;
import com.shoe.platform.model.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
	Orden save(Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario(Usuario usuario);
	Optional<Orden> findById(Integer id);
}
