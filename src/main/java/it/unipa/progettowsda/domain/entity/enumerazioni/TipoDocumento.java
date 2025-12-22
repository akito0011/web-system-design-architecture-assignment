package it.unipa.progettowsda.domain.entity.enumerazioni;

public enum TipoDocumento {
    CARTA_IDENTITA("Carta d'Identità"),
    PASSAPORTO("Passaporto"),
    PATENTE("Patente");

    private final String label;

    TipoDocumento(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
