package it.unipa.progettowsda.security;
import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.repository.UtenteRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    public CustomUserDetailsService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // cerchiamo l'utente tramite mail
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        //converto l'utente in un user di springboot security
        //(spring aggiunge già di base _ROLE all'utente)
        return User.builder()
                .username(utente.getEmail())
                .password(utente.getPassword()) // Spring si aspetta "{noop}password" o hash
                .roles(utente.getRuolo().name()) // "ADMIN", "CLIENTE", "STAFF"
                .build();
    }
}
