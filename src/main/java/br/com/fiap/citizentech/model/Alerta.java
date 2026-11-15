package br.com.fiap.citizentech.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Getter
@Setter
@NoArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelRisco nivel;

    @Column(nullable = false, length = 255)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAlerta status = StatusAlerta.ATIVO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "regiao_id", nullable = false)
    private Regiao regiao;

    // leitura que originou o alerta (opcional: pode ser um alerta manual)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leitura_id")
    private Leitura leitura;

    @PrePersist
    void antesDeSalvar() {
        criadoEm = LocalDateTime.now();
    }
}