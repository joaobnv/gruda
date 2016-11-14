package br.bancogrudand.entidades;

/**
 * Created by João Breno on 12/09/2016.
 */
public class Semestre {

    public static final String NOME_DA_TABELA = "semestre";
    public static final String ID = "id";
    public static final String DESCRICAO = "descricao";

    private final long id;
    private final String descricao;

    public Semestre(long id, String descricao) {
        if(id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("O id deve ser maior que ou igual a 0.");
        }
        if(descricao != null) {
            if (!descricao.matches("\\d+\\.[12]")) {
                throw new IllegalArgumentException("O argumento é inválido");
            }
        }
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public boolean equals(Object o) { //só analiza o id
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Semestre semestre = (Semestre) o;

        return id == semestre.id;

    }

    @Override
    public int hashCode() {  //só analiza o id
        return (int) (id ^ (id >>> 32));
    }
}
