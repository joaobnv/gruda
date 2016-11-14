package br.bancogrudand.entidades;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class Curso {

    //metadado de mapeamento
    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "curso";

    //nome das colunas da tabela no banco de dados
    public static final String ID = "id";
    public static final String DESCRICAO = "descricao";

    private final long id;
    private final String descricao;

    public Curso(long id, String descricao) {
        if(id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("O id deve ser maior que 0.");
        }
        this.descricao = descricao;
    }

    //métodos getter e setter
    public long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public boolean equals(Object o) { //só analisa o id
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Curso curso = (Curso) o;

        return id == curso.id;

    }

    @Override
    public int hashCode() { //só analisa o id
        return (int) (id ^ (id >>> 32));
    }
}
