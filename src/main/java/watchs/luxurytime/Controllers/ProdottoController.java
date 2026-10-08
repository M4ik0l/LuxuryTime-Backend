package watchs.luxurytime.Controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import watchs.luxurytime.Entities.Prodotto;
import watchs.luxurytime.Services.ProdottoService;
import watchs.luxurytime.Support.Messaggio;
import watchs.luxurytime.Support.exceptions.ProdottoEsistenteException;
import watchs.luxurytime.Support.exceptions.ProdottoInesistenteException;

import java.util.List;

@RestController
@RequestMapping("/prodotto")
public class ProdottoController {

    private final ProdottoService prodottoService;

    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Prodotto> getProdottoById(@PathVariable int id) throws ProdottoInesistenteException {
        return ResponseEntity.ok(prodottoService.getProdottoById(id));
    }

    @GetMapping("/get_paginate")
    public ResponseEntity<?> visualizzazionePaginata(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                     @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                                     @RequestParam(value = "ordinamento", defaultValue = "nome", required = false) String ordinamento) {
        List<Prodotto> result = prodottoService.visualizzazionePaginata(pageNumber, pageSize, ordinamento);
        if (result.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Prodotto> registraProdotto(@RequestBody @Valid Prodotto prodotto) throws ProdottoEsistenteException {
        return ResponseEntity.ok(prodottoService.registraProdotto(prodotto));
    }

    @PutMapping("/update")
    public ResponseEntity<Prodotto> aggiornaProdotto(@RequestBody @Valid Prodotto prodotto) throws ProdottoInesistenteException {
        return ResponseEntity.ok(prodottoService.aggiornaProdotto(prodotto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Messaggio> eliminaProdotto(@RequestParam(value = "id") int id) throws ProdottoInesistenteException {
        prodottoService.eliminaProdotto(id);
        return ResponseEntity.ok(new Messaggio("Prodotto eliminato con successo."));
    }

    @PostMapping("/ricerca_avanzata")
    public ResponseEntity<List<Prodotto>> ricercaAvanzata(@RequestBody Prodotto filtro) {
        List<Prodotto> risultato = prodottoService.ricercaAvanzataDinamica(filtro);
        return ResponseEntity.ok(risultato);
    }


    @GetMapping("/get_all")
    public ResponseEntity<List<Prodotto>> getProdotti() {
        List<Prodotto> tuttiIProdotti = prodottoService.getProdotti();
        return ResponseEntity.ok(tuttiIProdotti);
    }

    @PutMapping("/{id}/toggle-visibilita")
    public ResponseEntity<Messaggio> toggleVisibilita(@PathVariable int id) throws ProdottoInesistenteException {
        prodottoService.toggleVisibilita(id);
        return ResponseEntity.ok(new Messaggio("Visibilità aggiornata con successo."));
    }

}
