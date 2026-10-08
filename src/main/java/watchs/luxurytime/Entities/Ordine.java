package watchs.luxurytime.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "ordine", schema = "luxurytime_db")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data", nullable = false)
    private Date data;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utente")
    @JsonIgnoreProperties({"storicoOrdini", "carrello", "indirizzi"})
    private Utente acquirente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "indirizzo")
    private Indirizzo spedizione;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProdottoInCarrello> carrello;

    @Column(name = "stato")
    private String stato = "In Lavorazione";

    public Ordine(){
    }
}