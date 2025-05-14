package org.pl.securityservice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String googleId;

    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;
}

