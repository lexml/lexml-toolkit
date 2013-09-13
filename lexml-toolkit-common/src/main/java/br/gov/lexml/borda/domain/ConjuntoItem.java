
package br.gov.lexml.borda.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "conjunto_item")
public class ConjuntoItem implements Serializable {

    private static final long serialVersionUID = -7605533296886187021L;

    @Id
    @Column(name = "id_conjunto_item", length = 25)
    private String idConjuntoItem;

    @Column(name = "de_conjunto_item", nullable = false, length = 255)
    private String deConjuntoItem;

    @OneToMany(mappedBy = "conjuntoItem")
    private Set<RegistroItem> registroItemCollection;

    public ConjuntoItem() {
        super();
    }

    public String getIdConjuntoItem() {
        return idConjuntoItem;
    }

    public void setIdConjuntoItem(final String idConjuntoItem) {
        this.idConjuntoItem = idConjuntoItem;
    }

    public String getDeConjuntoItem() {
        return deConjuntoItem;
    }

    public void setDeConjuntoItem(final String deConjuntoItem) {
        this.deConjuntoItem = deConjuntoItem;
    }

    public Set<RegistroItem> getRegistroItemCollection() {
        return registroItemCollection;
    }

    public void setRegistroItemCollection(final Set<RegistroItem> registroItemCollection) {
        this.registroItemCollection = registroItemCollection;
    }

}
