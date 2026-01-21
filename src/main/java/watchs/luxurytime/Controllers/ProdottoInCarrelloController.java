package watchs.luxurytime.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import watchs.luxurytime.Entities.Prodotto;
import watchs.luxurytime.Entities.ProdottoInCarrello;
import watchs.luxurytime.Services.ProdottoInCarrelloService;
import watchs.luxurytime.Services.ProdottoService;
import watchs.luxurytime.Support.Messaggio;
import watchs.luxurytime.Support.exceptions.ProdottoEsauritoException;
import watchs.luxurytime.Support.exceptions.ProdottoInesistenteException;
import watchs.luxurytime.Support.exceptions.UtenteInesistenteException;

import java.util.List;

@RestController
@RequestMapping("/carrello")
public class ProdottoInCarrelloController {

    private final ProdottoInCarrelloService prodottoInCarrelloService;
    private final ProdottoService prodottoService;

    public ProdottoInCarrelloController(ProdottoInCarrelloService prodottoInCarrelloService, ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
        this.prodottoInCarrelloService = prodottoInCarrelloService;
    }

    @GetMapping("/get_carrello")
    public ResponseEntity<List<ProdottoInCarrello>> getCarrello(@AuthenticationPrincipal Jwt principal) throws UtenteInesistenteException {
        String email = principal.getClaimAsString("email");
        return ResponseEntity.ok(prodottoInCarrelloService.getCarrello(email));
    }

    @DeleteMapping("/elimina")
    public ResponseEntity<Messaggio> eliminaDaCarrello(@AuthenticationPrincipal Jwt principal, @RequestParam(value="codice") int codice) {
        String email = principal.getClaimAsString("email");
        prodottoInCarrelloService.diminuisciQuantitaProdottoInCarrello(codice, email);
        return new ResponseEntity<>(new Messaggio("Quantità diminuita."), HttpStatus.OK);
    }

    @DeleteMapping("/elimina_tutto")
    public ResponseEntity<Messaggio> eliminatutto(@AuthenticationPrincipal Jwt principal, @RequestParam(value="codice") int codice) {
        String email = principal.getClaimAsString("email");
        prodottoInCarrelloService.rimuoviProdottoInCarrello(codice, email);
        return ResponseEntity.ok(new Messaggio("Prodotto rimosso dal carrello."));
    }

    @PostMapping("/add_prodotto")
    public ResponseEntity<Messaggio> addProdotto(@AuthenticationPrincipal Jwt principal,
                                                 @RequestParam(value="codice") int codice,
                                                 @RequestParam(value="quantita") int quantita)
            throws UtenteInesistenteException, ProdottoInesistenteException, ProdottoEsauritoException {
        String email = principal.getClaimAsString("email");
        Prodotto p = prodottoService.getProdottoById(codice);
        prodottoInCarrelloService.add(email, p, quantita);
        return ResponseEntity.ok(new Messaggio("Prodotto aggiunto al carrello!"));
    }
}