package watchs.luxurytime.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import watchs.luxurytime.Enum.Genere;
import watchs.luxurytime.Enum.Materiale_Cassa;
import watchs.luxurytime.Enum.Materiale_Cinturino;
import watchs.luxurytime.Enum.Movimento;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "prodotti", schema = "luxurytime_db")
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "marca", nullable = false)
    private String marca;

    @Column(name = "modello", nullable = false)
    private String modello;

    @Column(name = "prezzo", nullable = false)
    private BigDecimal prezzo;

    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "prezzo_scontato")
    private BigDecimal prezzoScontato;

    @Column(name = "quantita", nullable = false)
    private Integer quantita;

    @Column(name = "anno", nullable = false)
    private Integer anno;

    @Enumerated(EnumType.STRING)
    @Column(name = "genere", nullable = false)
    private Genere genere;

    @Enumerated(EnumType.STRING)
    @Column(name = "movimento", nullable = false)
    private Movimento movimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "materiale_cassa", nullable = false)
    private Materiale_Cassa materialeCassa;

    @Enumerated(EnumType.STRING)
    @Column(name = "materiale_cinturino", nullable = false)
    private Materiale_Cinturino materialeCinturino;

    @Column(name = "diametro_cassa", nullable = false)
    private String diametroCassa;

    @Column(name = "edizione_limitata", nullable = false)
    private boolean edizioneLimitata;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "prodotto_immagini", joinColumns = {@JoinColumn(name = "prodotto_id")})
    @Column(name = "url_immagine", columnDefinition = "TEXT")
    private List<String> immagini;

    @Column(name = "colore_quadrante", nullable = false)
    private String coloreQuadrante;

    @Column(name = "tipo_vetro", nullable = false)
    private String tipoVetro;

    @Column(name = "resistenza_acqua", nullable = false)
    private String resistenzaAcqua;

    @Column(name = "circonferenza_polso", nullable = false)
    private String circonferenzaPolso;

    @Column(name = "condizione", nullable = false)
    private String condizione;

    @Column(name = "garanzia", nullable = false)
    private String garanzia;

    @Column(name = "collezione", nullable = false)
    private String collezione;

    @Column(name = "riferimento", nullable = false)
    private String riferimento;

    @Column(name = "certificato", nullable = false)
    private String certificato;

    @Column(name = "chiusura", nullable = false)
    private String chiusura;

    @Column(name = "attivo", nullable = false)
    private boolean attivo = true;

    @OneToMany(targetEntity = ProdottoInCarrello.class, mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProdottoInCarrello> prodottiNelCarrello;

    public Prodotto() {
    }
}