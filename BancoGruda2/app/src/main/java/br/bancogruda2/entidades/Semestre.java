package br.bancogruda2.entidades;

/**
 * Created by LAB01 on 12/09/2016.
 */
public class Semestre {

    public static final String NOME_DA_TABELA = "semestre";
    public static final String ID = "id";
    public static final String NUMERO = "numero";

    private long id;
    private long numero;

    public Semestre() {
    }

    public Semestre(long numero) {
        this.numero = numero;
    }

    public Semestre(long id, long numero) {
        this.id = id;
        this.numero = numero;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if(id >= 0)
            this.id = id;
        else
            throw new IllegalArgumentException("O id deve ser >= 0.");
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }
}
