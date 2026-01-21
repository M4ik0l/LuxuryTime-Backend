package watchs.luxurytime.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "prodotti_in_carrello", schema = "luxurytime_db")
public class ProdottoInCarrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @Column(name = "prezzo", nullable = false)
    private BigDecimal prezzo;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "prodotto", referencedColumnName = "id")
    private Prodotto prodotto;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "utente", referencedColumnName = "id")
    @JsonIgnore
    private Utente utente;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ordine", referencedColumnName = "id")
    @JsonIgnore
    private Ordine ordine;

    @Column(name = "data_aggiunta", nullable = false)
    private LocalDateTime dataAggiunta;

    public ProdottoInCarrello(Prodotto prod, int quantita, BigDecimal prezzoPassato, Utente u){
        if (prod.getPrezzoScontato() != null && prod.getPrezzoScontato().compareTo(BigDecimal.ZERO) > 0) {
            this.prezzo = prod.getPrezzoScontato();
        } else {
            this.prezzo = prod.getPrezzo();
        }
        this.quantita = quantita;
        this.prodotto = prod;
        this.utente = u;
        this.dataAggiunta = LocalDateTime.now();
    }

    public ProdottoInCarrello(){
        this.dataAggiunta = LocalDateTime.now();
    }
}