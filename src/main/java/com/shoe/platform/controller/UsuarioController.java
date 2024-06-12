package com.shoe.platform.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shoe.platform.model.Orden;
import com.shoe.platform.model.Usuario;
import com.shoe.platform.service.IOrdenService;
import com.shoe.platform.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService iUsuarioService;

	@Autowired
	private IOrdenService iOrdenService;

	@GetMapping("/registro")
	public String create() {
		return "usuario/registro";
	}

	@PostMapping("/save")
	public String save(Usuario usuario) {
		logger.info("Usuario registro: {}", usuario);
		usuario.setTipo("USER");
		iUsuarioService.save(usuario);

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "usuario/login";
	}

	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		logger.info("accesos: {}", usuario);

		Optional<Usuario> user = iUsuarioService.findByEmail(usuario.getEmail());
		// logger.info("Usuario de db: {}", user.get());

		if (user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());
			if (user.get().getTipo().equals("USER")) {
				return "redirect:/";
			} else {
				return "redirect:/administrador";
			}
		} else {
			logger.info("Usuario no existe");
		}

		return "redirect:/";
	}

	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		model.addAttribute("sesion", session.getAttribute("idusuario"));

		Usuario usuario = iUsuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString()))
				.get();
		List<Orden> ordenes = iOrdenService.findByUsuario(usuario);

		model.addAttribute("ordenes", ordenes);

		return "usuario/compras";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		
		logger.info("Id de la orden: {}", id);
		Optional<Orden> orden = iOrdenService.findById(id);
		
		model.addAttribute("detalles", orden.get().getDetalle());
		
		//session
		model.addAttribute("sesion", model.getAttribute("idusuario"));
		return "usuario/detallecompra";
	}

}
