package it.unipa.progettowsda.domain.service;

import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.entity.enumerazioni.Ruolo;
import it.unipa.progettowsda.domain.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Meglio usare quello di Spring

import java.util.List;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepo;
    private final PasswordEncoder passwordEncoder;

    // Ho corretto 'passwordEndcoder' in 'passwordEncoder'
    public UtenteService(UtenteRepository utenteRepo, PasswordEncoder passwordEncoder) {
        this.utenteRepo = utenteRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // --- METODI ESISTENTI ---

    public Utente registraCliente(String nome, String cognome, String email, String password) {
        if (utenteRepo.existsByEmail(email)) {
            throw new RuntimeException("Email già registrata!");
        }
        Utente u = new Utente();
        u.setNome(nome);
        u.setCognome(cognome);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRuolo(Ruolo.CLIENTE);
        return utenteRepo.save(u);
    }

    public Utente findByEmail(String email) {
        return utenteRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato: " + email));
    }

    public Utente findById(Integer id) {
        return utenteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
    }

    // --- NUOVI METODI PER GESTIONE STAFF (Quelli che mancavano) ---

    public List<Utente> getTuttoLoStaff() {
        return utenteRepo.findByRuolo(Ruolo.STAFF);
    }

    @Transactional
    public void creaStaff(Utente u) {
        // Controllo se esiste già l'email
        if (utenteRepo.findByEmail(u.getEmail()).isPresent()) {
            throw new RuntimeException("Email già presente nel sistema.");
        }

        // Impostiamo forzatamente il ruolo STAFF e criptiamo la password
        u.setRuolo(Ruolo.STAFF);
        u.setPassword(passwordEncoder.encode(u.getPassword()));

        utenteRepo.save(u);
    }

    @Transactional
    public void eliminaUtente(Integer id) {
        if (!utenteRepo.existsById(id)) {
            throw new RuntimeException("Utente non trovato");
        }
        utenteRepo.deleteById(id);
    }
}