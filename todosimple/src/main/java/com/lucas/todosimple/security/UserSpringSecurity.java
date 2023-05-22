package com.lucas.todosimple.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lucas.todosimple.models.enums.ProfileEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSpringSecurity implements UserDetails{

    private Long id;
    private String userName;
    private String passWord;
    private Collection<? extends GrantedAuthority> authorities;


    public UserSpringSecurity(Long id, String userName, String passWord, Set<ProfileEnum> profileEnums) {
        this.id = id;
        this.userName = userName;
        this.passWord = passWord;
        // Mapeando os perfis (profileEnums) para uma lista de SimpleGrantedAuthority
        this.authorities = profileEnums.stream().map(x -> new SimpleGrantedAuthority(x.getDescricao())) // Mapeia cada perfil para SimpleGrantedAuthority
        .collect(Collectors.toList()); // Coleta todos os elementos mapeados em uma nova lista
    }

    @Override
    public String getPassword() {
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
    }

    @Override
    public boolean isAccountNonExpired() { 
        /*
         * Tempo de expiração da conta.
         * Depois voltar aqui para implementar um limite para ver como é.
         * Esta como true porque agora nesse momento não vai ter conta com limite de expiração.
         */
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        /*
         * Vai verificar se a conta esta travada, bloqueada.
         * Por enquanto não vai ter isso.
         */
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        /*
         * Verifcar se a credencial esta expirada.
         */
        return true;
    }

    @Override
    public boolean isEnabled() {
        /*
         * Vai verificar se a conta esta ativa.
         */
        return true;
    } 
    
    public boolean hasRole(ProfileEnum profileEnum){
        /*
         * Verificar se o perfil contem um Profile.
         */
        return getAuthorities().contains(new SimpleGrantedAuthority(profileEnum.getDescricao()));
    }
}
