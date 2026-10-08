package watchs.luxurytime.Support.exceptions;

public class TelefonoMancanteException extends Exception {
    public TelefonoMancanteException() {
        super("Numero di telefono mancante. È necessario fornirne uno per la spedizione.");
    }
}