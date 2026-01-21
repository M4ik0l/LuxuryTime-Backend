package watchs.luxurytime.Controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import watchs.luxurytime.Entities.Indirizzo;
import watchs.luxurytime.Entities.Utente;
import watchs.luxurytime.Services.UtenteService;
import watchs.luxurytime.Support.Messaggio;
import watchs.luxurytime.Support.exceptions.OrdineInesistenteException;
import watchs.luxurytime.Support.exceptions.UtenteEsistenteException;
import watchs.luxurytime.Support.exceptions.UtenteInesistenteException;

import java.util.List;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @PostMapping("/crea_utente")
    public ResponseEntity<Utente> registraUtente(@RequestBody @Valid Utente utente) throws UtenteEsistenteException {
        return new ResponseEntity<>(utenteService.registraUtente(utente), HttpStatus.OK);
    }

    @DeleteMapping("/elimina_utente")
    public ResponseEntity<Messaggio> eliminaUtente(@RequestParam(value = "id_utente") int id) throws UtenteInesistenteException, OrdineInesistenteException {
        utenteService.eliminaUtente(id);
        return ResponseEntity.ok(new Messaggio("Utente eliminato con successo!"));
    }

    @GetMapping("/ricerca_avanzata")
    public ResponseEntity<List<Utente>> ricercaAvanzata(@RequestParam(value = "nome", required = false) String nome,
                                                        @RequestParam(value = "cognome", required = false) String cognome,
                                                        @RequestParam(value = "email", required = false) String email) {
        return ResponseEntity.ok(utenteService.ricercaAvanzata(nome, cognome, email));
    }

    @PatchMapping("/aggiorna_telefono")
    public ResponseEntity<?> aggiornaTelefono(@AuthenticationPrincipal Jwt principal,
                                              @RequestParam(value = "id_utente", required = false) Integer id,
                                              @RequestParam(value = "telefono") String telefono) {
        try {
            if (id != null) {
                Utente utenteAggiornato = utenteService.aggiornaTelefono(id, telefono);
                return new ResponseEntity<>(utenteAggiornato, HttpStatus.OK);
            } else {
                String email = principal.getClaimAsString("email");
                Utente u = utenteService.ricercaAvanzata(null, null, email).stream().findFirst().orElseThrow(UtenteInesistenteException::new);
                Utente utenteAggiornato = utenteService.aggiornaTelefono(u.getId(), telefono);
                return new ResponseEntity<>(utenteAggiornato, HttpStatus.OK);
            }
        } catch (UtenteInesistenteException e) {
            return new ResponseEntity<>(new Messaggio(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/aggiorna")
    public ResponseEntity<Utente> aggiornaUtente(@RequestBody Utente utente) {
        try {
            Utente utenteAggiornato = utenteService.aggiornaUtente(utente);
            return new ResponseEntity<>(utenteAggiornato, HttpStatus.OK);
        } catch (UtenteInesistenteException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/tutti")
    public ResponseEntity<List<Utente>> getAllUtenti() {
        return ResponseEntity.ok(utenteService.findAll());
    }

    @GetMapping("/indirizzi")
    public ResponseEntity<List<Indirizzo>> getIndirizzi(@AuthenticationPrincipal Jwt principal) throws UtenteInesistenteException {
        String email = principal.getClaimAsString("email");
        return ResponseEntity.ok(utenteService.getIndirizziUtente(email));
    }
}