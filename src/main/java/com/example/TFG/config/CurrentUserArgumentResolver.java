package com.example.TFG.config;

import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.UsuarioService;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver
        implements HandlerMethodArgumentResolver {

    private final UsuarioService usuarioService;

    public CurrentUserArgumentResolver(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(Usuario.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return null;
        }

        return usuarioService.buscarPorEmail(auth.getName());
    }
}