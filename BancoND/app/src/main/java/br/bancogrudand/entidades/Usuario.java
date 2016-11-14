package br.bancogrudand.entidades;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class Usuario {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "usuario";

    //nome das colunas da tabela no banco de dados
    public static final String ID = "id";
    public static final String NOME = "nome";
    public static final String LINK_FACEBOOK = "linkfacebook";

    private long id;
    private String nome;
    private String linkFacebook;

    public Usuario() {

    }

    public Usuario(String nome, String linkFacebook) {

        this.nome = nome;
        this.linkFacebook = linkFacebook;
    }

    public Usuario(long id, String nome, String linkFacebook)
            throws IllegalArgumentException {

        setId(id);
        this.nome = nome;
        this.linkFacebook = linkFacebook;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLinkFacebook() {
        return linkFacebook;
    }

    public void setLinkFacebook(String linkFacebook) {
        this.linkFacebook = linkFacebook;
    }

    @Override
    public boolean equals(Object o) { //só analisa o id
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        return id == usuario.id;

    }

    @Override
    public int hashCode() { //só analisa o id
        return (int) (id ^ (id >>> 32));
    }
}
