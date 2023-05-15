package com.lucas.todosimple.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private static final String[] PUBLIC_MATCHERS = { //Qual a rota do sistema é publica 
        "/"
    };

    private static final String[] PUBLIC_MATCHERS_POST = { //Qual a rota do sistema é publica para post
        "/user",
        "/login"
    };
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable(); //desativa proteção de varios request que o spring cria(Para ambiente de desenvolvimento é ok desativar).
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // define que o Spring Security não criará ou usará sessões HTTP para armazenar informações de autenticação.
       // http.authorizeRequests().antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll().antMatchers(PUBLIC_MATCHERS).permitAll().anyRequest().authenticated();
        return http.build();
    }
        
} 
