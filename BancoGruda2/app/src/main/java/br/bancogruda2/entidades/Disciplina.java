package br.bancogruda2.entidades;

/**
 * Created by JOÃO BRENO on 01/09/2016.
 */
public class Disciplina {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "curso";

    //nome das colunas da tabela no banco de dados
    public static final String ID = "id";
    public static final String DESCRICAO = "descricao";
    public static final String CURSO_ID = "curso_id";

    private long id;
    private String descricao;
    private Curso curso;

    public Disciplina() {
    }

    public Disciplina(String descricao, Curso curso) {
        this.descricao = descricao;
        setCurso(curso);
    }

    public Disciplina(long id, String descricao, Curso curso) {
        setId(id);
        this.descricao = descricao;
        setCurso(curso);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if(id >= 0)
            this.id = id;
        else
            throw new IllegalArgumentException("O id deve ser maior que ou igual a 0.");
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        if(id > 0 && curso.getId() <= 0) {
            throw new IllegalArgumentException("O curso já deve ter sido armazenado no banco de dados.");
        }
        this.curso = curso;
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
