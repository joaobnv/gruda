package br.bancogruda2.entidades;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import br.bancogruda2.DAO.RepositorioDisciplina;
import br.bancogruda2.DAO.RepositorioDisciplinaTemUsuarioTemSemestreTemNota;

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
    private Set<DisciplinaTemUsuarioTemSemestreTemNota> disciplinaTemUsuarioTemSemestreTemNotaSet
            = new HashSet<DisciplinaTemUsuarioTemSemestreTemNota>();

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

    public boolean addDisciplinaSeuSemestreESuaNota(Disciplina disciplina, Semestre semestre, Nota nota, Context ctx)
            throws NullPointerException{

        if(disciplina == null || semestre == null || nota == null) {
            throw new NullPointerException("Os argumentos não podem ser null. é preferível que você" +
                    " passe um objeto que foi criado com o construtor sem argumentos.");
        }

        DisciplinaTemUsuarioTemSemestreTemNota discTemUsuTemSemTemNota =
                new DisciplinaTemUsuarioTemSemestreTemNota();


        discTemUsuTemSemTemNota.setDisciplina(disciplina);
        discTemUsuTemSemTemNota.setUsuarioTemSemestre(new UsuarioTemSemestre(this, semestre));
        discTemUsuTemSemTemNota.setNota(nota);

        RepositorioDisciplinaTemUsuarioTemSemestreTemNota rDTUTSTN = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        long retorno = rDTUTSTN.inserir(discTemUsuTemSemTemNota);
        rDTUTSTN.close();

        if(retorno < 0) {
            return false;
        }

        return disciplinaTemUsuarioTemSemestreTemNotaSet.add(discTemUsuTemSemTemNota);
    }

    public boolean addDisciplinaESeuSemestre(Disciplina disciplina, Semestre semestre, Context ctx)
            throws NullPointerException {
        if(disciplina == null || semestre == null) {
            throw new NullPointerException("Os argumentos não podem ser null. é preferível que você" +
                    " passe um objeto que foi criado com o construtor sem argumentos.");
        }

        return addDisciplinaSeuSemestreESuaNota(disciplina, semestre, new Nota(), ctx);
    }

    public boolean mudarNota(Disciplina disciplina, Semestre semestre, Nota mudeParaEstaNota, Context ctx)
            throws NullPointerException{

        if(disciplina == null || semestre == null || mudeParaEstaNota == null) {
            throw new NullPointerException("Os argumentos não podem ser null. é preferível que você" +
                    " passe um objeto que foi criado com o construtor sem argumentos.");
        }

        for(DisciplinaTemUsuarioTemSemestreTemNota dusn : disciplinaTemUsuarioTemSemestreTemNotaSet) {
            if(dusn.getDisciplina().equals(disciplina)) {
                if(dusn.getUsuarioTemSemestre().getSemestre().equals(semestre)) {

                    DisciplinaTemUsuarioTemSemestreTemNota dusnAntigo = dusn;
                    dusn.setNota(mudeParaEstaNota);

                    RepositorioDisciplinaTemUsuarioTemSemestreTemNota rDTUTSTN
                            = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
                    long retorno = rDTUTSTN.atualizar(dusnAntigo, dusn);
                    rDTUTSTN.close();
                    if(retorno < 0) {
                        dusn.setNota(dusnAntigo.getNota());
                        return false;
                    }

                    return true;
                }
            }
        }
        return false;
    }

    public void remove(Disciplina disciplina, Semestre semestre, Nota nota, Context ctx) {

        RepositorioDisciplinaTemUsuarioTemSemestreTemNota rdusn
                = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        rdusn.deletar(disciplina, this, semestre, nota);

        if(disciplina == null && semestre == null && nota == null) {
            disciplinaTemUsuarioTemSemestreTemNotaSet.clear();
        } else if(disciplina == null && semestre == null && nota != null) {
            for(DisciplinaTemUsuarioTemSemestreTemNota dusn : disciplinaTemUsuarioTemSemestreTemNotaSet) {
                if(dusn.getNota().equals(nota)) {
                    disciplinaTemUsuarioTemSemestreTemNotaSet.remove(dusn);
                }
            }
        } else if(disciplina == null && semestre != null && nota == null) {
            for(DisciplinaTemUsuarioTemSemestreTemNota dusn : disciplinaTemUsuarioTemSemestreTemNotaSet) {
                if(dusn.getUsuarioTemSemestre().getSemestre().equals(semestre)) {
                    disciplinaTemUsuarioTemSemestreTemNotaSet.remove(dusn);
                }
            }
        } else if(disciplina == null && semestre != null && nota != null) {
            for(DisciplinaTemUsuarioTemSemestreTemNota dusn : disciplinaTemUsuarioTemSemestreTemNotaSet) {
                if(dusn.getUsuarioTemSemestre().getSemestre().equals(semestre)) {
                    if(dusn.getNota().equals(nota)) {
                        disciplinaTemUsuarioTemSemestreTemNotaSet.remove(dusn);
                    }
                }
            }
        } else if(disciplina != null && semestre == null && nota == null) {
            for(DisciplinaTemUsuarioTemSemestreTemNota dusn : disciplinaTemUsuarioTemSemestreTemNotaSet) {
                if(dusn.getDisciplina().equals(disciplina)) {
                    disciplinaTemUsuarioTemSemestreTemNotaSet.remove(dusn);
                }
            }
        } else if(disciplina != null && semestre == null && nota != null) {
            for(DisciplinaTemUsuarioTemSemestreTemNota dusn : disciplinaTemUsuarioTemSemestreTemNotaSet) {
                if(dusn.getDisciplina().equals(disciplina)) {
                    if(dusn.getNota().equals(nota)) {
                        disciplinaTemUsuarioTemSemestreTemNotaSet.remove(dusn);
                    }
                }
            }
        } else if(disciplina != null && semestre != null && nota == null) {
            for(DisciplinaTemUsuarioTemSemestreTemNota dusn : disciplinaTemUsuarioTemSemestreTemNotaSet) {
                if(dusn.getDisciplina().equals(disciplina)) {
                    if(dusn.getUsuarioTemSemestre().getSemestre().equals(semestre)) {
                        disciplinaTemUsuarioTemSemestreTemNotaSet.remove(dusn);
                    }
                }
            }
        } else if(disciplina != null && semestre != null && nota != null) {
            DisciplinaTemUsuarioTemSemestreTemNota dusn
                    = new DisciplinaTemUsuarioTemSemestreTemNota(disciplina, new UsuarioTemSemestre(this, semestre),
                            nota);
            disciplinaTemUsuarioTemSemestreTemNotaSet.remove(dusn);
        }
    }

    public Set<DisciplinaTemUsuarioTemSemestreTemNota> getDisciplinaTemUsuarioTemSemestreTemNotaSet() {
        return disciplinaTemUsuarioTemSemestreTemNotaSet;
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
