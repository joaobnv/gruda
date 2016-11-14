package br.bancogrudand.entidades;

/**
 * Created by JOÃO BRENO on 01/09/2016.
 */
public class Disciplina {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "disciplina";

    //nome das colunas da tabela no banco de dados
    public static final String ID = "id";
    public static final String DESCRICAO = "descricao";
    public static final String CURSO_ID = "curso_id";

    private final long id;
    private final String descricao;
    private final Curso curso;

    public Disciplina(long id, String descricao, Curso curso) {
        if(id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("O id deve ser maior que ou igual a 0.");
        }
        this.descricao = descricao;
        this.curso = curso;
    }

    public long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Curso getCurso() {
        return curso;
    }

    @Override
    public boolean equals(Object o) { //só analisa o id
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Disciplina that = (Disciplina) o;

        return id == that.id;

    }

    @Override
    public int hashCode() { //só analisa o id
        return (int) (id ^ (id >>> 32));
    }
}
