package it.unipa.progettowsda.web.controller;
import it.unipa.progettowsda.domain.service.UtenteService;
import it.unipa.progettowsda.web.form.RegistrazioneForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UtenteService utenteService;

    public AuthController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    // 1. Mostra il form vuoto
    @GetMapping("/login")
    public String login() {
        return "public/login";
    }

    // 2. Mostra il form di registrazione
    @GetMapping("/register")
    public String register(Model model) {
        // Passiamo un DTO vuoto all'HTML per "ospitare" i dati
        model.addAttribute("form", new RegistrazioneForm());
        return "public/register";
    }

    // 3. Riceve i dati, li VALIDA e prova a salvare
    @PostMapping("/register")
    public String doRegister(
            @Valid @ModelAttribute("form") RegistrazioneForm form, // @Valid per fare tutti i controlli definiti nel form DTO
            BindingResult result,                                  // BindingResult serve a fare un check di tutti gli errori subito dopo la validazione
            Model model) {

        // prima controlliamo se ci sono errori di validazione tipo password corta o campi vuoti
        if (result.hasErrors()) {
            // Se non ci sono errori chiamiamo il service
            // Torniamo alla pagina "register". Thymeleaf vedrà gli errori in 'result' e li mostrerà.
            return "public/register";
        }

        // invece se i dati sono formalmente corretti, proviamo a salvare nel DB
        try {
            utenteService.registraCliente(
                    form.getNome(),
                    form.getCognome(),
                    form.getEmail(),
                    form.getPassword()
            );
            // Successo! Rimandiamo al login
            return "redirect:/login?registered";

        } catch (RuntimeException e) {
            // PASSO C: Gestione errore "Business" (es. Email già usata nel DB)
            // Questo errore non viene catturato da @Valid perché richiede una query al DB.
            // Lo aggiungiamo manualmente agli errori globali.
            model.addAttribute("errorMessage", e.getMessage());
            return "public/register";
        }
    }
}
