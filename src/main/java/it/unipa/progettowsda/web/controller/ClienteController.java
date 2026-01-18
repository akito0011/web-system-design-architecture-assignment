package it.unipa.progettowsda.web.controller;

import it.unipa.progettowsda.domain.entity.Prenotazione;
import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.service.PrenotazioneService;
import it.unipa.progettowsda.domain.service.UtenteService;
import it.unipa.progettowsda.web.form.CheckInForm;
import it.unipa.progettowsda.web.form.OspiteForm;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;

    public ClienteController(PrenotazioneService prenotazioneService, UtenteService utenteService) {
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Utente utente = utenteService.findByEmail(userDetails.getUsername());
        List<Prenotazione> tutte = prenotazioneService.getPrenotazioneUtente(utente.getId());
        LocalDate oggi = LocalDate.now();

        List<Prenotazione> viaggioAttuale = tutte.stream()
                .filter(p -> p.getStato() == it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione.IN_CORSO)
                .filter(p -> !oggi.isBefore(p.getDataCheckin()) && !oggi.isAfter(p.getDataCheckout()))
                .collect(Collectors.toList());

        List<Prenotazione> prenotazioniConfermate = tutte.stream()
                .filter(p -> p.getStato() == it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione.CONFERMATA)
                .sorted(Comparator.comparing(Prenotazione::getDataCheckin))
                .collect(Collectors.toList());

        List<Prenotazione> storico = tutte.stream()
                .filter(p -> p.getStato() == it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione.TERMINATA
                        || p.getStato() == it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione.CANCELLATA)
                .sorted((p1, p2) -> p2.getDataCheckin().compareTo(p1.getDataCheckin()))
                .collect(Collectors.toList());

        model.addAttribute("utente", utente);
        model.addAttribute("viaggioAttuale", viaggioAttuale);
        model.addAttribute("prenotazioniConfermate", prenotazioniConfermate);
        model.addAttribute("storico", storico);

        return "cliente/dashboard";
    }

    // GESTIONE CHECK-IN

    //  GET: Mostra la pagina con il form pre-generato
    @GetMapping("/checkin/{id}")
    public String mostraCheckIn(@PathVariable Integer id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {

        Utente utente = utenteService.findByEmail(userDetails.getUsername());
        Prenotazione prenotazione = prenotazioneService.findById(id);

        if (!prenotazione.getUtente().getId().equals(utente.getId())) {
            return "redirect:/cliente/dashboard?error=NonAutorizzato";
        }

        if (prenotazione.getStato() != it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione.CONFERMATA) {
            return "redirect:/cliente/dashboard?error=StatoNonValido";
        }

        CheckInForm form = new CheckInForm();
        form.setIdPrenotazione(id);

        for (int i = 0; i < prenotazione.getNumOspiti(); i++) {
            OspiteForm ospite = new OspiteForm();

            if (i == 0) {
                ospite.setNome(utente.getNome());
                ospite.setCognome(utente.getCognome());
                // Se hai aggiunto dataNascita a Utente, decommenta questa riga:
                // ospite.setDataNascita(utente.getDataNascita());
            }
            form.getOspiti().add(ospite);
        }

        model.addAttribute("checkInForm", form);
        model.addAttribute("prenotazione", prenotazione);

        return "cliente/checkin";
    }

    // POST: Riceve i dati, valida e salva
    @PostMapping("/conferma-checkin")
    public String salvaCheckIn(
            @Valid @ModelAttribute("checkInForm") CheckInForm form, // @Valid triggera il controllo su OspiteForm.dataNascita
            BindingResult result,
            Model model) {

        //  Recuperiamo la prenotazione per confrontare i dati
        Prenotazione prenotazione = prenotazioneService.findById(form.getIdPrenotazione());

        //  Validazione errori standard (campi vuoti, date nulle)
        if (result.hasErrors()) {
            model.addAttribute("prenotazione", prenotazione);
            return "cliente/checkin";
        }

        //  VALIDAZIONE DOCUMENTI CAPOGRUPPO
        if (!form.getOspiti().isEmpty()) {
            OspiteForm capogruppo = form.getOspiti().getFirst();

            if (capogruppo.getTipoDoc() == null) {
                result.rejectValue("ospiti[0].tipoDoc", "error.doc", "Il tipo di documento è obbligatorio per il capogruppo");
            }
            if (capogruppo.getNumeroDoc() == null || capogruppo.getNumeroDoc().trim().isEmpty()) {
                result.rejectValue("ospiti[0].numeroDoc", "error.doc", "Il numero documento è obbligatorio");
            }
        }

        //  VALIDAZIONE ESENZIONI TASSA DI SOGGIORNO
        int esenzioniDichiarate = prenotazione.getNumOspitiEsentiDichiarati();

        if (esenzioniDichiarate > 0) {
            long esentiEffettivi = 0;
            LocalDate oggi = LocalDate.now();

            // Controlliamo TUTTI gli ospiti (incluso capogruppo)
            for (OspiteForm ospite : form.getOspiti()) {
                if (ospite.getDataNascita() != null) {
                    long anni = ChronoUnit.YEARS.between(ospite.getDataNascita(), oggi);

                    // Criterio: Minori di 12 anni O Maggiori di 85
                    if (anni < 12 || anni > 85) {
                        esentiEffettivi++;
                    }
                }
            }

            // Se gli ospiti che hanno diritto all'esenzione sono MENO di quelli dichiarati -> ERRORE
            if (esentiEffettivi < esenzioniDichiarate) {
                result.reject("error.esenzioni",
                        "Hai dichiarato " + esenzioniDichiarate + " esenzioni, ma in base alle date di nascita inserite " +
                                "solo " + esentiEffettivi + " ospite/i ha/hanno diritto (età < 12 o > 85).");
            }
        }

        //  RICARICA PAGINA SE CI SONO STATI ERRORI DI LOGICA (Doc o Esenzioni)
        if (result.hasErrors()) {
            model.addAttribute("prenotazione", prenotazione);
            return "cliente/checkin";
        }

        //  TUTTO OK -> SALVA (Il service farà il ricalcolo prezzo in base alle date nascita se necessario)
        prenotazioneService.eseguiCheckIn(form.getIdPrenotazione(), form.getOspiti());

        return "redirect:/cliente/dashboard?success=CheckInEffettuato";
    }

    // ALTRI METODI CHECK-OUT E NOTE
    @GetMapping("/checkout/{id}")
    public String mostraPaginaCheckout(@PathVariable Integer id,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        Utente utente = utenteService.findByEmail(userDetails.getUsername());
        Prenotazione p = prenotazioneService.findById(id);

        if (!p.getUtente().getId().equals(utente.getId())) return "redirect:/cliente/dashboard?error=NonAutorizzato";
        if (p.getStato() != it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione.IN_CORSO) return "redirect:/cliente/dashboard?error=StatoNonValido";

        model.addAttribute("prenotazione", p);
        model.addAttribute("capogruppo", utente);
        return "cliente/checkout";
    }

    @PostMapping("/conferma-checkout")
    public String confermaCheckOut(@RequestParam Integer idPrenotazione, @AuthenticationPrincipal UserDetails userDetails) {
        Utente utente = utenteService.findByEmail(userDetails.getUsername());
        Prenotazione p = prenotazioneService.findById(idPrenotazione);
        if (!p.getUtente().getId().equals(utente.getId())) return "redirect:/cliente/dashboard?error=NonAutorizzato";
        prenotazioneService.eseguiCheckOut(idPrenotazione);
        return "redirect:/cliente/dashboard?success=CheckOutEffettuato";
    }

    @PostMapping("/invia-nota")
    public String inviaNota(@RequestParam Integer idPrenotazione, @RequestParam String nota, @AuthenticationPrincipal UserDetails userDetails) {
        Utente utente = utenteService.findByEmail(userDetails.getUsername());
        Prenotazione p = prenotazioneService.findById(idPrenotazione);
        if (!p.getUtente().getId().equals(utente.getId())) return "redirect:/cliente/dashboard?error=NonAutorizzato";
        prenotazioneService.aggiungiNota(idPrenotazione, nota);
        return "redirect:/cliente/dashboard?success=NotaInviata";
    }
}