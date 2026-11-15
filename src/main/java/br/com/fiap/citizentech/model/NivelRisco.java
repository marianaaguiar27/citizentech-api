package br.com.fiap.citizentech.model;

public enum NivelRisco {
    BAIXO, MEDIO, ALTO, CRITICO;

    // Limiares em cm de água. Regra única, usada na classificação das leituras.
    public static NivelRisco classificar(double nivelAguaCm) {
        if (nivelAguaCm >= 150) return CRITICO;
        if (nivelAguaCm >= 100) return ALTO;
        if (nivelAguaCm >= 50) return MEDIO;
        return BAIXO;
    }

    public boolean exigeAlerta() {
        return this == ALTO || this == CRITICO;
    }
}