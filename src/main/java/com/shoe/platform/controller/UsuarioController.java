package com.shoe.platform.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shoe.platform.model.DetalleOrden;
import com.shoe.platform.model.Orden;
import com.shoe.platform.model.Usuario;
import com.shoe.platform.service.IOrdenService;
import com.shoe.platform.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
	@ResponseBody
	public Map<String, String> acceder(@RequestBody Usuario usuario, HttpSession session) {
		Map<String, String> response = new HashMap<>();

		try {
			Optional<Usuario> user = iUsuarioService.findByEmail(usuario.getEmail());
			if (user.isPresent() && usuario.getPassword().equals(user.get().getPassword())) {
				session.setAttribute("idusuario", user.get().getId());
				session.setAttribute("tipoUsuario", user.get().getTipo());

				response.put("status", "success");
				response.put("message", "Acceso exitoso");
				response.put("redirect", user.get().getTipo().equals("USER") ? "/" : "/administrador");
			} else {
				response.put("status", "error");
				response.put("message", "Correo o contraseña incorrectos");
			}
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error al intentar iniciar sesión");
		}

		return response;
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

		// session
		model.addAttribute("sesion", model.getAttribute("idusuario"));
		return "usuario/detallecompra";
	}

	@GetMapping("/archivoPdf/{id}")
	public String archivoPdf(@PathVariable("id") Integer Id, HttpSession session, Model model) {
		Object idUsuario = session.getAttribute("idusuario");

		if (idUsuario != null) {
			Optional<Usuario> optionalUsuario = iUsuarioService.findById(Integer.parseInt(idUsuario.toString()));
			Orden orden = iOrdenService.findById(Id).get();
			List<DetalleOrden> detalles = orden.getDetalle();

			Usuario usuario = optionalUsuario.get();
			model.addAttribute("cart", detalles);
			model.addAttribute("orden", orden);
			model.addAttribute("usuario", usuario);
			return "usuario/pdf";
		}
		return "usuario/login";
	}

	@GetMapping("/cerrar")
	public String cerrarSesion(HttpSession session) {
		session.removeAttribute("idusuario");
		return "redirect:/";
	}

}
