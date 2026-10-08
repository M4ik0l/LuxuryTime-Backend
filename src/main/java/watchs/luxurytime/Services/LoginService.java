package watchs.luxurytime.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import watchs.luxurytime.Entities.Utente;
import watchs.luxurytime.Repositories.UtenteRepository;

import java.util.Optional;

@Service
public class LoginService {

    private final UtenteRepository utenteRepository;

    public LoginService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }


    @Transactional
    public Utente addOrgetUtente(String nome, String cognome, String email, String numeroTelefonico){

        Optional<Utente> utenteEsistente = utenteRepository.findByEmail(email);
        if (utenteEsistente.isPresent()) {
            Utente utente = utenteEsistente.get();
            utente.setNome(nome);
            utente.setCognome(cognome);
            if (numeroTelefonico != null && !numeroTelefonico.isEmpty()) {
                utente.setNumeroTelefonico(numeroTelefonico);
            }
            return utenteRepository.save(utente);
        } else {
            Utente nuovoUtente = new Utente(nome, cognome, email, numeroTelefonico);
            return utenteRepository.save(nuovoUtente);
        }
    }
}