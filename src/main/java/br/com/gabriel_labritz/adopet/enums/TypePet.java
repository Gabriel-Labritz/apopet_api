package br.com.gabriel_labritz.adopet.enums;

public enum TypePet {
    GATO("GATO"),
    CACHORRO("CACHORRO");

    private String typePet;

    TypePet(String type) {
        this.typePet = type;
    }

    public static TypePet toPetType(String typePet) {
        for (TypePet type: TypePet.values()) {
            if (type.typePet.equalsIgnoreCase(typePet)) {
                return type;
            }
        }

        throw new IllegalArgumentException("O tipo " + typePet + " não existe no enum TypePet");
    }
}
