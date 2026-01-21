package watchs.luxurytime.Controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import watchs.luxurytime.Entities.Indirizzo;
import watchs.luxurytime.Entities.Ordine;
import watchs.luxurytime.Services.OrdineService;
import watchs.luxurytime.Support.Messaggio;
import watchs.luxurytime.Support.exceptions.*;

import java.util.List;

@RestController
@RequestMapping("/ordine")
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    @PostMapping("/add_ordine")
    public ResponseEntity<Ordine> effettuaOrdine(@AuthenticationPrincipal Jwt principal,
                                                 @RequestParam(value = "quantitaAttesa", required = false) Integer quantitaAttesa,
                                                 @RequestBody @Valid Indirizzo spedizione)
            throws CarrelloVuotoException, UtenteInesistenteException,
            PrezzoCambiatoException, TelefonoMancanteException {

        String email = principal.getClaimAsString("email");
        return ResponseEntity.ok(ordineService.effettuaOrdine(email, spedizione, quantitaAttesa));
    }

    @GetMapping("/get_acquisti")
    public ResponseEntity<List<Ordine>> getOrdini(@AuthenticationPrincipal Jwt principal) throws UtenteInesistenteException {
        String email = principal.getClaimAsString("email");
        return ResponseEntity.ok(ordineService.getOrdiniUtente(email));
    }

    @GetMapping("/tutti")
    public ResponseEntity<List<Ordine>> getAllOrdini() {
        return ResponseEntity.ok(ordineService.findAll());
    }

    @DeleteMapping("/elimina")
    public ResponseEntity<Messaggio> eliminaOrdine(@RequestParam(value = "id") int id) throws OrdineInesistenteException {
        ordineService.eliminaOrdine(id);
        return ResponseEntity.ok(new Messaggio("Ordine cancellato correttamente."));
    }

    @PatchMapping("/aggiorna_stato")
    public ResponseEntity<Ordine> aggiornaStato(@RequestParam("id") int id,
                                                @RequestParam("stato") String stato) throws OrdineInesistenteException {
        return ResponseEntity.ok(ordineService.aggiornaStato(id, stato));
    }
}