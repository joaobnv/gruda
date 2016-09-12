package br.bancogruda2.entidades;

/**
 * Created by JOÃO BRENO on 02/09/2016.
 */
public class UsuarioTemSemestre {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "usuario_has_semestre";

    //nome das colunas da tabela no banco de dados
    public static final String USUARIO_ID = "usuario_id";
    public static final String SEMESTRE_ID = "semestre_id";

    private Usuario usuario;
    private Semestre semestre;

    public UsuarioTemSemestre() {
    }

    public UsuarioTemSemestre(Usuario usuario, Semestre semestre) {
        this.usuario = usuario;
        this.semestre = semestre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioTemSemestre that = (UsuarioTemSemestre) o;

        if (usuario != null ? !usuario.equals(that.usuario) : that.usuario != null) return false;
        return semestre != null ? semestre.equals(that.semestre) : that.semestre == null;

    }

    @Override
    public int hashCode() {
        int result = usuario != null ? usuario.hashCode() : 0;
        result = 31 * result + (semestre != null ? semestre.hashCode() : 0);
        return result;
    }
}
