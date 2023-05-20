package com.lucas.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.lucas.todosimple.models.User;
import com.lucas.todosimple.models.enums.ProfileEnum;
import com.lucas.todosimple.repositories.UserRepository;
import com.lucas.todosimple.services.exceptions.DataBindingViolationException;
import com.lucas.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException( "Usuário não encontrado! Id: " + id + ", tipo: " + User.class.getName()));
    }

    @Transactional
    public User create(User user){
        user.setPassWord(this.bCryptPasswordEncoder.encode(user.getPassWord()));
        user.setProfiles(Stream.of(ProfileEnum.USER.getId()).collect(Collectors.toSet()));
        user = this.userRepository.save(user);
        return user;
    }

    public User updatePassWord(User user){
        User newUser = findById(user.getId());
        newUser.setPassWord(user.getPassWord());
        newUser.setPassWord(this.bCryptPasswordEncoder.encode(user.getPassWord()));
        return this.userRepository.save(newUser);
    }

    public void delete(Long id){
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há uma entidades relacionada!");
        }
    }
}
