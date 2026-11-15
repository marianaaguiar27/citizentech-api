package br.com.fiap.citizentech.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leituras")
@Getter
@Setter
@NoArgsConstructor
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double nivelAguaCm;

    private Double chuvaMm;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ponto_id", nullable = false)
    private PontoMonitoramento ponto;

    @PrePersist
    void antesDeSalvar() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }
}