package br.com.fiap.citizentech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pontos_monitoramento")
@Getter
@Setter
@NoArgsConstructor
public class PontoMonitoramento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "regiao_id", nullable = false)
    private Regiao regiao;
}