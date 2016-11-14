package br.bancogrudand.entidades;

/**
 * Created by JOÃO BRENO on 01/10/2016.
 */
public class UsuarioTemSemestreTemCurso {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "usuario_tem_semestre_tem_curso";

    //nome das colunas da tabela no banco de dados
    public static final String CURSO_ID = "curso_id";
    public static final String USU_TEM_SEM_USUARIO_ID = "usu_t_sem_usu_id";
    public static final String USU_TEM_SEM_SEMESTRE_ID = "usu_t_sem_sem_id";

    private Curso curso;
    private UsuarioTemSemestre usuarioTemSemestre;

    public UsuarioTemSemestreTemCurso() {
    }

    public UsuarioTemSemestreTemCurso(UsuarioTemSemestre usuarioTemSemestre, Curso curso) {
        this.curso = curso;
        this.usuarioTemSemestre = usuarioTemSemestre;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public UsuarioTemSemestre getUsuarioTemSemestre() {
        return usuarioTemSemestre;
    }

    public void setUsuarioTemSemestre(UsuarioTemSemestre usuarioTemSemestre) {
        this.usuarioTemSemestre = usuarioTemSemestre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioTemSemestreTemCurso that = (UsuarioTemSemestreTemCurso) o;

        if (curso != null ? !curso.equals(that.curso) : that.curso != null) return false;
        return usuarioTemSemestre != null ? usuarioTemSemestre.equals(that.usuarioTemSemestre) : that.usuarioTemSemestre == null;

    }

    @Override
    public int hashCode() {
        int result = curso != null ? curso.hashCode() : 0;
        result = 31 * result + (usuarioTemSemestre != null ? usuarioTemSemestre.hashCode() : 0);
        return result;
    }
}
