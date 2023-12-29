package br.com.motur.dealbackendservice.core.model.common;

public enum Color {
    BLACK("Preto"),
    WHITE("Branco"),
    RED("Vermelho"),
    BLUE("Azul"),
    SILVER("Prata"),
    GRAY("Cinza"),
    GREEN("Verde"),
    YELLOW("Amarelo"),
    ORANGE("Laranja"),
    PURPLE("Roxo"),
    BROWN("Marrom"),
    GOLD("Dourado"),
    BEIGE("Bege"),
    PINK("Rosa"),
    OTHER("Outra");

    private final String displayName;

    Color(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
