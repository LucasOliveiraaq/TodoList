package com.lucas.todosimple.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.lucas.todosimple.models.User;
import com.lucas.todosimple.repositories.UserRepository;
import com.lucas.todosimple.security.UserSpringSecurity;

public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository; 

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUserName(username);
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException("Usuario não encontrado: " + username);
        }
        return new UserSpringSecurity(user.getId(), user.getUserNome(), user.getPassWord(), user.getProfiles());
    }
    
}
