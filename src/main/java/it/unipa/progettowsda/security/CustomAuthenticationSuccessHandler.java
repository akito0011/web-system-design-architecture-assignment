package it.unipa.progettowsda.security;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
//questa clase serve a gestire la fase subito dopo l'autenticazione, smistando chi fa l'accesso alla pagina corretta (cliente, gestore o amministratore)
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Recuperiamo i ruoli dell'utente appena loggato
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_STAFF")) {
            response.sendRedirect("/staff/dashboard");
        } else {
            // Default: Cliente
            response.sendRedirect("/cliente/dashboard");
        }
    }
}
