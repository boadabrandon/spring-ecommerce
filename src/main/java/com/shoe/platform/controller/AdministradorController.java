package com.shoe.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shoe.platform.model.Producto;
import com.shoe.platform.service.IUsuarioService;
import com.shoe.platform.service.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService iUsuarioService;
	
	@GetMapping("")
	public String home(Model model) {
		
		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos",productos);
		return "administrador/home";
	}
	
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", iUsuarioService.findAll());
		return "administrador/usuarios";
	}
}
