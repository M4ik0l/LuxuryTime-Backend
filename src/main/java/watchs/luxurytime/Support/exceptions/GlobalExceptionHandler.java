package watchs.luxurytime.Support.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import watchs.luxurytime.Support.Messaggio;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UtenteInesistenteException.class)
    public ResponseEntity<Messaggio> handleUtenteInesistente() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("Utente inesistente."));
    }

    @ExceptionHandler(IndirizzoInesistenteException.class)
    public ResponseEntity<Messaggio> handleIndirizzoInesistente() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("L'indirizzo indicato non esiste o l'utente non ha indirizzi."));
    }

    @ExceptionHandler(IndirizzoEsistenteException.class)
    public ResponseEntity<Messaggio> handleIndirizzoEsistente() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("L'indirizzo indicato esiste già."));
    }

    @ExceptionHandler(CarrelloVuotoException.class)
    public ResponseEntity<Messaggio> handleCarrelloVuoto() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("Il carrello è vuoto."));
    }

    @ExceptionHandler(ProdottoEsauritoException.class)
    public ResponseEntity<Messaggio> handleProdottoEsaurito() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("La quantità che si vuole acquistare non è più disponibile."));
    }

    @ExceptionHandler(ProdottoInesistenteException.class)
    public ResponseEntity<Messaggio> handleProdottoInesistente() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("Prodotto non disponibile o inesistente nel sistema."));
    }

    @ExceptionHandler(PrezzoCambiatoException.class)
    public ResponseEntity<Messaggio> handlePrezzoCambiato() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("Il prezzo del prodotto è cambiato."));
    }

    @ExceptionHandler(ProdottoEsistenteException.class)
    public ResponseEntity<Messaggio> handleProdottoEsistente() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("Il prodotto è già presente nel sistema."));
    }

    @ExceptionHandler(UtenteEsistenteException.class)
    public ResponseEntity<Messaggio> handleUtenteEsistente() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("L'utente esiste già."));
    }

    @ExceptionHandler(OrdineInesistenteException.class)
    public ResponseEntity<Messaggio> handleOrdineInesistente() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Messaggio("L'ordine cercato non esiste."));
    }

    @ExceptionHandler(TelefonoMancanteException.class)
    public ResponseEntity<Messaggio> handleTelefonoMancante(TelefonoMancanteException ex) {
        return new ResponseEntity<>(new Messaggio(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
