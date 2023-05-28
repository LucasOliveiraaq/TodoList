package com.lucas.todosimple.security;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lucas.todosimple.exceptions.GlobalExceptionHandler;
import com.lucas.todosimple.models.User;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private JWTUtil jwtUtil;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        setAuthenticationFailureHandler(new GlobalExceptionHandler());
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        try {
            User userCredentials = new ObjectMapper().readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userCredentials.getUsername(), userCredentials.getPassWord(), new ArrayList<>());

            Authentication authentication = this.authenticationManager.authenticate(authToken);
            return authentication;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /*
     * Quando uma autenticação é considerada bem-sucedida.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain , Authentication authentication) throws IOException, ServletException{
        UserSpringSecurity userSpringSecurity = (UserSpringSecurity) authentication.getPrincipal(); //Vai pegar o usuario dps de uma autenticação bem-sucedida.
        String userName = userSpringSecurity.getUsername();
        String token = this.jwtUtil.generateToken(userName);
        /*
         * Essa duas linhas de codigo, em um cenario que o token deve ser transmitido para o usuario.
         */
        response.addHeader("Authorization", "Bearer " + token); //O Authorization é usado para enviar informações de autenticação ou autorização para o servidor. 
        response.addHeader("access-control-expose-headers", "Authorization"); //Pode ser acessado pelo javascript, para mostra p o usuario.
    }
}
