package com.udea.fe.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter

public class UserTeamId  implements Serializable {
    private Long userId;
    private Long teamId;

    // Constructor sin argumentos (requerido por JPA)
    public UserTeamId() {}

    // Constructor con argumentos (opcional pero útil)
    public UserTeamId(Long userId, Long teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }
}
