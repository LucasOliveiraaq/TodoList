package com.lucas.todosimple.models.enums;

import java.util.Objects;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProfileEnum {
    ADMIN(1, "ROLE_ADMIN"),
    USER(2, "ROLE_USER");

    private Integer id;
    private String descricao;

    public static ProfileEnum toEnum(Integer id) {
        if (Objects.isNull(id))
            return null;

        for (ProfileEnum p : ProfileEnum.values()) {
            if(id.equals(p.getId()))
                return p;
            }
            throw new IllegalArgumentException("Id invalido" + id);
    }
}
