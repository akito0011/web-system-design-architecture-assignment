package it.unipa.progettowsda.web.form;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CheckInForm {

    @NotNull(message = "ID Prenotazione mancante")
    private Integer idPrenotazione;

    @NotEmpty(message = "La lista ospiti non può essere vuota")
    @Valid // questo serve ad abilitare la validazione degli oggetti dentro la lista
    private List<OspiteForm> ospiti = new ArrayList<>();

    // --- GETTER E SETTER ---

    public Integer getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(Integer idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public List<OspiteForm> getOspiti() {
        return ospiti;
    }

    public void setOspiti(List<OspiteForm> ospiti) {
        this.ospiti = ospiti;
    }
}