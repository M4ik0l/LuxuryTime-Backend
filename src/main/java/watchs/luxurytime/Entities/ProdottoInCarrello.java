package watchs.luxurytime.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "prodottiInCarrello", schema = "luxurytime_db")
public class ProdottiInCarrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @Column(name = "prezzo", nullable = false)
    private Double prezzo;


    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "prodotto", referencedColumnName = "id")
    private Prodotto prodotto;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "utente", referencedColumnName = "id")
    @JsonIgnore
    private Utente utente;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ordine", referencedColumnName = "id")
    @JsonIgnore
    private Ordine ordine;

    public ProdottiInCarrello(Prodotto prod, int quantita, Double prezzo, Utente u){
        this.prezzo = prezzo;
        this.quantita = quantita;
        this.prodotto = prod;
        this.utente = u;
    }

    public ProdottiInCarrello(){

    }
}
