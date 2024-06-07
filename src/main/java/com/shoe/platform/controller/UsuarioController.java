package com.shoe.platform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shoe.platform.model.Usuario;
import com.shoe.platform.service.IUsuarioService;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private IUsuarioService iUsuarioService;

    @GetMapping("/registro")
    public String create(){
        return "usuario/registro";
    }

    @PostMapping("/save")
    public String save(Usuario usuario){
        logger.info("Usuario registro: {}", usuario);
        usuario.setTipo("USER");
        
        return "redirect:/";
    }

    
    
}
