package br.com.fiap.citizentech.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "regioes")
@Getter
@Setter
@NoArgsConstructor
public class Regiao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 80)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelRisco nivelRisco = NivelRisco.BAIXO;

    @OneToMany(mappedBy = "regiao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<PontoMonitoramento> pontos = new ArrayList<>();

    public void adicionarPonto(PontoMonitoramento ponto) {
        pontos.add(ponto);
        ponto.setRegiao(this);
    }

    public void removerPonto(PontoMonitoramento ponto) {
        pontos.remove(ponto);
        ponto.setRegiao(null);
    }
}