package it.unipa.progettowsda.web.controller;

import it.unipa.progettowsda.domain.service.DomoticaService;
import it.unipa.progettowsda.domain.entity.Tapparella;
import it.unipa.progettowsda.domain.entity.Termostato;
import it.unipa.progettowsda.domain.entity.enumerazioni.Azione;
import it.unipa.progettowsda.domain.entity.enumerazioni.ModalitaTermostato;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente/domotica")
public class DomoticaController {

    private final DomoticaService domoticaService;

    public DomoticaController(DomoticaService domoticaService) {
        this.domoticaService = domoticaService;
    }

    // --- VISTA PRINCIPALE (GET) ---
    @GetMapping("/{id}")
    public String dashboard(@PathVariable("id") Integer idPrenotazione, Model model) {
        model.addAttribute("idPrenotazione", idPrenotazione);

        // Recuperiamo le Luci (che tornano una lista, quindi mai eccezione, al massimo vuota)
        try {
            model.addAttribute("listaLuci", domoticaService.getLuciByPrenotazioneId(idPrenotazione));
        } catch (EntityNotFoundException e) {
            model.addAttribute("erroreGenerale", "Prenotazione o Camera non trovata.");
            return "cliente/domotica"; // Esci subito se non c'è la camera
        }

        // Recuperiamo Tapparella (gestiamo il caso in cui non esista senza rompere la pagina)
        try {
            model.addAttribute("tapparella", domoticaService.getTapparellaByPrenotazioneId(idPrenotazione));
        } catch (EntityNotFoundException e) {
            model.addAttribute("tapparella", null); // Non c'è, pazienza
        }

        // Recuperiamo Termostato (gestiamo il caso in cui non esista)
        try {
            model.addAttribute("termostato", domoticaService.getTermostatoByPrenotazioneId(idPrenotazione));
        } catch (EntityNotFoundException e) {
            model.addAttribute("termostato", null);
        }

        return "cliente/domotica";
    }

    // --- AZIONI TAPPARELLA (POST) ---
    @PostMapping("/{id}/tapparelle/regola")
    public String muoviTapparella(
            @PathVariable("id") Integer idPrenotazione, // ID Prenotazione dall'URL
            @RequestParam Azione azione,
            RedirectAttributes redirectAttributes) {

        try {
            //  Recupero l'oggetto Tapparella corretto tramite la prenotazione
            Tapparella t = domoticaService.getTapparellaByPrenotazioneId(idPrenotazione);

            //  Eseguo l'azione usando l'ID reale della tapparella
            domoticaService.regolaTapparella(t.getId(), azione);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/cliente/domotica/" + idPrenotazione;
    }

    // --- AZIONI TERMOSTATO: TEMPERATURA (POST) ---
    @PostMapping("/{id}/termostati/regola")
    public String cambiaTemperatura(
            @PathVariable("id") Integer idPrenotazione,
            @RequestParam Azione azione,
            RedirectAttributes redirectAttributes) {

        try {
            //  Recupero il Termostato corretto
            Termostato t = domoticaService.getTermostatoByPrenotazioneId(idPrenotazione);

            //  Eseguo l'azione usando l'ID reale del termostato
            domoticaService.regolaTemperatura(t.getId(), azione);

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/cliente/domotica/" + idPrenotazione;
    }

    // --- AZIONI TERMOSTATO: MODALITÀ (POST) ---
    @PostMapping("/{id}/termostati/modalita")
    public String cambiaModalita(
            @PathVariable("id") Integer idPrenotazione,
            @RequestParam ModalitaTermostato modalita,
            RedirectAttributes redirectAttributes) {

        try {
            //  Recupero il Termostato corretto
            Termostato t = domoticaService.getTermostatoByPrenotazioneId(idPrenotazione);

            //  Cambio modalità usando l'ID reale del termostato
            domoticaService.cambiaModalita(t.getId(), modalita);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/cliente/domotica/" + idPrenotazione;
    }

    @PostMapping("/{id}/luci/switch")
    public String switchLuce(
            @PathVariable("id") Integer idPrenotazione,
            @RequestParam Integer idLuce,
            RedirectAttributes redirectAttributes) {
        try {
            domoticaService.switchLuce(idLuce);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore luci: " + e.getMessage());
        }
        return "redirect:/cliente/domotica/" + idPrenotazione;
    }
}