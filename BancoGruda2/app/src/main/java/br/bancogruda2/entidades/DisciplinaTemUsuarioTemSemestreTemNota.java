package br.bancogruda2.entidades;

/**
 * Created by JOÃO BRENO on 05/09/2016.
 */
public class DisciplinaTemUsuarioTemSemestreTemNota {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "disciplina_has_usuario_has_semestre";

    //nome das colunas da tabela no banco de dados
    public static final String DISCIPLINA_ID = "disciplina_id";
    public static final String USUARIO_HAS_SEMESTRE_USUARIO_ID = "usuario_has_semestre_usuario_id";
    public static final String USUARIO_HAS_SEMESTRE_SEMESTRE_ID = "usuario_has_semestre_semestre_id";
    public static final String NOTA_ID = "nota_id";

    private Disciplina disciplina;
    private UsuarioTemSemestre usuarioTemSemestre;
    private Nota nota;

    public DisciplinaTemUsuarioTemSemestreTemNota() {
    }

    public DisciplinaTemUsuarioTemSemestreTemNota(Disciplina disciplina, UsuarioTemSemestre usuarioTemSemestre, Nota nota) {
        this.disciplina = disciplina;
        this.usuarioTemSemestre = usuarioTemSemestre;
        this.nota = nota;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public UsuarioTemSemestre getUsuarioTemSemestre() {
        return usuarioTemSemestre;
    }

    public void setUsuarioTemSemestre(UsuarioTemSemestre usuarioTemSemestre) {
        this.usuarioTemSemestre = usuarioTemSemestre;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisciplinaTemUsuarioTemSemestreTemNota that = (DisciplinaTemUsuarioTemSemestreTemNota) o;

        if (disciplina != null ? !disciplina.equals(that.disciplina) : that.disciplina != null)
            return false;
        if (usuarioTemSemestre != null ? !usuarioTemSemestre.equals(that.usuarioTemSemestre) : that.usuarioTemSemestre != null)
            return false;
        return nota != null ? nota.equals(that.nota) : that.nota == null;

    }

    @Override
    public int hashCode() {
        int result = disciplina != null ? disciplina.hashCode() : 0;
        result = 31 * result + (usuarioTemSemestre != null ? usuarioTemSemestre.hashCode() : 0);
        result = 31 * result + (nota != null ? nota.hashCode() : 0);
        return result;
    }
}
