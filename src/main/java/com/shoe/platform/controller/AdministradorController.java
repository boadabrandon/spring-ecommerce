package com.shoe.platform.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shoe.platform.model.Orden;
import com.shoe.platform.model.Producto;
import com.shoe.platform.service.IOrdenService;
import com.shoe.platform.service.IUsuarioService;
import com.shoe.platform.service.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	private static final Logger log = LoggerFactory.getLogger(AdministradorController.class);

	@Autowired
	private ProductoService productoService;

	@Autowired
	private IUsuarioService iUsuarioService;

	@Autowired
	private IOrdenService iOrdenService;

	@GetMapping("")
	public String home(Model model) {

		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);
		return "administrador/home";
	}

	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", iUsuarioService.findAll());
		return "administrador/usuarios";
	}

	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		model.addAttribute("ordenes", iOrdenService.findAll());
		return "administrador/ordenes";
	}

	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id) {
		log.info("Id de la orden: {}", id);
		Orden orden = iOrdenService.findById(id).get();

		model.addAttribute("detalles", orden.getDetalle());
		return "administrador/detalleorden";
	}
}
