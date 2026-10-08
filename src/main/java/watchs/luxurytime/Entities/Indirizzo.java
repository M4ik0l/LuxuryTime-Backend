package watchs.luxurytime.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
@Table(name = "indirizzo", schema = "luxurytime_db")
public class Indirizzo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "citta", nullable = false)
    private String citta;

    @Column(name = "via", nullable = false)
    private String via;

    @Column(name = "numero_civico", nullable = false)
    private int numeroCivico;

    @Column(name = "cap", nullable = false)
    private int cap;

    @Column(name = "attivo", nullable = false)
    private boolean attivo;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "utente", referencedColumnName = "id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Utente utente;

    public Indirizzo() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Indirizzo indirizzo)) return false;
        if (id != 0 && indirizzo.id != 0) {
            return id == indirizzo.id;
        }
        return numeroCivico == indirizzo.numeroCivico
                && cap == indirizzo.cap
                && Objects.equals(citta, indirizzo.citta)
                && Objects.equals(via, indirizzo.via);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(numeroCivico, cap, citta, via);
    }
}