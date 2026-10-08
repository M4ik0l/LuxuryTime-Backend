package watchs.luxurytime.Controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import watchs.luxurytime.Entities.Indirizzo;
import watchs.luxurytime.Services.IndirizzoService;
import watchs.luxurytime.Support.Messaggio;
import watchs.luxurytime.Support.exceptions.IndirizzoEsistenteException;
import watchs.luxurytime.Support.exceptions.IndirizzoInesistenteException;
import watchs.luxurytime.Support.exceptions.UtenteInesistenteException;

import java.util.List;

@RestController
@RequestMapping("/indirizzo")
public class IndirizzoController {

    private final IndirizzoService indirizzoService;

    public IndirizzoController(IndirizzoService indirizzoService) {
        this.indirizzoService = indirizzoService;
    }

    @GetMapping("/get_indirizzi_utente")
    public ResponseEntity<List<Indirizzo>> getAllIndirizziUtente(@AuthenticationPrincipal Jwt principal) throws UtenteInesistenteException, IndirizzoInesistenteException {
        String email = principal.getClaimAsString("email");
        return ResponseEntity.ok(indirizzoService.getAllIndirizziUtente(email));
    }

    @GetMapping("/get")
    public ResponseEntity<List<Indirizzo>> getIndirizzo(@RequestParam(value = "citta") String citta, @RequestParam(value = "via") String via, @RequestParam(value = "numeroC") int numeroC) throws IndirizzoInesistenteException {
        return ResponseEntity.ok(indirizzoService.getIndirizzo(citta,via,numeroC));
    }

    @PostMapping("/add")
    public ResponseEntity<Indirizzo> registraIndirizzo(@RequestBody @Valid Indirizzo indirizzo, @RequestParam(value = "id") int id) throws UtenteInesistenteException, IndirizzoEsistenteException {
        return ResponseEntity.ok(indirizzoService.registraIndirizzo(indirizzo,id));
    }

    @PutMapping("/update")
    public ResponseEntity<Indirizzo> aggiornaIndirizzo(@RequestBody @Valid Indirizzo indirizzo) throws IndirizzoInesistenteException {
        return ResponseEntity.ok(indirizzoService.aggiornaIndirizzo(indirizzo));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Messaggio> eliminaIndirizzo(@RequestParam(value = "id") int id) throws IndirizzoInesistenteException {
        indirizzoService.eliminaIndirizzo(id);
        return new ResponseEntity<>(new Messaggio("Indirizzo eliminato con successo"), HttpStatus.OK);
    }
}