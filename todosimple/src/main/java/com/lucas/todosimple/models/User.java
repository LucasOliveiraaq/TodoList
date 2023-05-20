package com.lucas.todosimple.models;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.lucas.todosimple.models.enums.ProfileEnum;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {

    private static final Access jsonpr = null;

    public interface CreateUser {}
    public interface UpdateUser {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "userNome", length = 100, nullable = false, unique = true)
    @NotNull(groups = CreateUser.class)
    @jakarta.validation.constraints.NotEmpty(groups = CreateUser.class)
    @jakarta.validation.constraints.Size(groups = CreateUser.class, min = 2, max = 100)
    private String userNome;

    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "password", length = 60, nullable = false)
    @NotNull(groups = { CreateUser.class, UpdateUser.class })
    @jakarta.validation.constraints.NotEmpty(groups = { CreateUser.class, UpdateUser.class })
    @jakarta.validation.constraints.Size(groups = { CreateUser.class, UpdateUser.class }, min = 8, max = 60)
    private String passWord;

    @OneToMany(mappedBy = "user")
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Task> tasks;

    @ElementCollection(fetch = FetchType.EAGER) //Carrega em um unico acesso para o banco.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //Propriedade só vai ser escrita pelo json, mais não vai ser lida.
    @CollectionTable(name = "user_profile")
    @Column(name = "profile", nullable = false)
    private Set<Integer> profiles = new HashSet<>();

    private Set<ProfileEnum> getProfiles(){
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(Collectors.toSet());
    }

    private void addProfile(ProfileEnum profileEnum){
        this.profiles.add(profileEnum.getId());
    }

}
