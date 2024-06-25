package com.shoe.platform.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shoe.platform.model.DetalleOrden;
import com.shoe.platform.model.Orden;
import com.shoe.platform.model.Producto;
import com.shoe.platform.model.Usuario;
import com.shoe.platform.service.IDetalleOrdenService;
import com.shoe.platform.service.IOrdenService;
import com.shoe.platform.service.IUsuarioService;
import com.shoe.platform.service.ProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private ProductoService productoService;

	@Autowired
	private IUsuarioService iUsuarioService;

	@Autowired
	private IOrdenService iOrdenService;

	@Autowired
	private IDetalleOrdenService iDetalleOrdenService;

	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	Orden orden = new Orden();

	@GetMapping("")
	public String home(Model model, HttpSession session) {

		log.info("Sesion del usuario: {}", session.getAttribute("idusuario"));

		model.addAttribute("productos", productoService.findAll());

		// session
		model.addAttribute("sesion", session.getAttribute("idusuario"));

		return "usuario/home";
	}

	@GetMapping("/productohome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {
		log.info("Id producto enviado como parametro {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();

		model.addAttribute("producto", producto);
		return "usuario/productohome";
	}

	@PostMapping("/cart")
	public String addCard(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {

		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;

		Optional<Producto> optionalProducto = productoService.get(id);
		log.info("Producto añadido: {}", optionalProducto.get());
		log.info("Cantidad: {}", cantidad);

		producto = optionalProducto.get();
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio() * cantidad);
		detalleOrden.setProducto(producto);

		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId() == idProducto);
		if (!ingresado) {
			detalles.add(detalleOrden);
		}

		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {

		List<DetalleOrden> ordenesNuevas = new ArrayList<DetalleOrden>();

		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNuevas.add(detalleOrden);
			}
		}
		detalles = ordenesNuevas;

		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {

		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "/usuario/carrito";
	}

	@GetMapping("/order")
	public String order(Model model, HttpSession session) {

		Object idUsuario = session.getAttribute("idusuario");

		if (idUsuario != null) {
			Optional<Usuario> optionalUsuario = iUsuarioService.findById(Integer.parseInt(idUsuario.toString()));

			if (optionalUsuario.isPresent()) {
				Usuario usuario = optionalUsuario.get();
				model.addAttribute("cart", detalles);
				model.addAttribute("orden", orden);
				model.addAttribute("usuario", usuario);
				return "usuario/resumenorden";
			}
		}
		return "usuario/login";
	}

	@GetMapping("/saveOrder")
	@ResponseBody
	public Map<String, String> saveOrder(HttpSession session) {
		Map<String, String> response = new HashMap<>();

		try {
			Date fechaCreacion = new Date();
			orden.setFechaCreacion(fechaCreacion);
			orden.setNumero(iOrdenService.generarNumeroOrden());

			Usuario usuario = iUsuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString()))
					.get();
			orden.setUsuario(usuario);
			iOrdenService.save(orden);

			for (DetalleOrden dt : detalles) {
				Producto producto = dt.getProducto();
				double nuevaCantidad = producto.getCantidad() - dt.getCantidad();
				producto.setCantidad(nuevaCantidad);
				productoService.save(producto);
				dt.setOrden(orden);
				iDetalleOrdenService.save(dt);
			}

			orden = new Orden();
			detalles.clear();

			response.put("status", "success");
			response.put("message", "Orden realizada con éxito");
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error al realizar la orden");
		}
		return response;
	}

	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre, Model model) {
		log.info("Nombre del producto: {}", nombre);
		List<Producto> productos = productoService.findAll().stream().filter(p -> p.getNombre().contains(nombre))
				.collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

}
