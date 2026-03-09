package br.com.gabriel_labritz.adopet.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "abrigo")
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(name = "telefone", nullable = false, unique = true)
    private String phone;

    @Setter
    @OneToMany(mappedBy = "shelter", fetch = FetchType.LAZY)
    private List<Pet> pets;
}
