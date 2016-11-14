package br.bancogrudand.entidades;

/**
 * Created by JOÃO BRENO on 01/10/2016.
 */
public class UsuarioTemSemestreTemCursoTemDisciplinaTemNota {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "usuario_tem_semestre_tem_curso_tem_disciplina_tem_nota";

    //nome das colunas da tabela no banco de dados
    public static final String USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID = "usu_t_sem_t_curso_usu_t_sem_usu_id";
    public static final String USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID = "usu_t_sem_t_curso_usu_t_sem_sem_id";
    public static final String USU_TEM_SEM_TEM_CURSO_CURSO_ID = "usu_t_sem_t_curso_curso_id";
    public static final String DISCIPLINA_ID = "disc_id";
    public static final String NOTA_ID = "nota_id";

    private UsuarioTemSemestreTemCurso usuTemSemTemCurso;
    private Disciplina disciplina;
    private Nota nota;

    public UsuarioTemSemestreTemCursoTemDisciplinaTemNota() {
    }

    public UsuarioTemSemestreTemCursoTemDisciplinaTemNota(UsuarioTemSemestreTemCurso usuTemSemTemCurso, Disciplina disciplina, Nota nota) {
        this.disciplina = disciplina;
        this.nota = nota;
        this.usuTemSemTemCurso = usuTemSemTemCurso;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    public UsuarioTemSemestreTemCurso getUsuTemSemTemCurso() {
        return usuTemSemTemCurso;
    }

    public void setUsuTemSemTemCurso(UsuarioTemSemestreTemCurso usuTemSemTemCurso) {
        this.usuTemSemTemCurso = usuTemSemTemCurso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioTemSemestreTemCursoTemDisciplinaTemNota that = (UsuarioTemSemestreTemCursoTemDisciplinaTemNota) o;

        if (disciplina != null ? !disciplina.equals(that.disciplina) : that.disciplina != null)
            return false;
        if (nota != null ? !nota.equals(that.nota) : that.nota != null) return false;
        return usuTemSemTemCurso != null ? usuTemSemTemCurso.equals(that.usuTemSemTemCurso) : that.usuTemSemTemCurso == null;

    }

    @Override
    public int hashCode() {
        int result = disciplina != null ? disciplina.hashCode() : 0;
        result = 31 * result + (nota != null ? nota.hashCode() : 0);
        result = 31 * result + (usuTemSemTemCurso != null ? usuTemSemTemCurso.hashCode() : 0);
        return result;
    }
}
