package com.example.encurtadorlink.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O nome do campo da class Link
    @OneToMany(mappedBy = "user")
    private List<Link> links;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 256)
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private RoleName role;
}
