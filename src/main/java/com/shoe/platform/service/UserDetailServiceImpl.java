package com.shoe.platform.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoe.platform.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuarioService iUsuarioService;

    @Autowired
    private BCryptPasswordEncoder bCrypt;

    @Autowired
    HttpSession session;

    private Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Este es el username");
        Optional<Usuario> optionalUser = iUsuarioService.findByEmail(username);
        if (optionalUser.isPresent()) {
            logger.info("Este es el Id del usuario: {}", optionalUser.get().getId());
            session.setAttribute("idusuario", optionalUser.get().getId());
            Usuario usuario = optionalUser.get();
            return User.builder().username(usuario.getNombre()).password(bCrypt.encode(usuario.getPassword()))
                    .roles(usuario.getTipo()).build();
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

    }

}
