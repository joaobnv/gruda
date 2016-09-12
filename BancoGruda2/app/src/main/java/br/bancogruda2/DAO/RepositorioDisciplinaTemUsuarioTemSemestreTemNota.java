package br.bancogruda2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.bancogruda2.entidades.Disciplina;
import br.bancogruda2.entidades.DisciplinaTemUsuarioTemSemestreTemNota;
import br.bancogruda2.entidades.Nota;
import br.bancogruda2.entidades.Semestre;
import br.bancogruda2.entidades.Usuario;
import br.bancogruda2.entidades.UsuarioTemSemestre;

/**
 * Created by JOÃO BRENO on 05/09/2016.
 */
public class RepositorioDisciplinaTemUsuarioTemSemestreTemNota implements Closeable {

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private boolean dbHelperAberto;

    public RepositorioDisciplinaTemUsuarioTemSemestreTemNota(Context ctx) {
        this.ctx = ctx;
        dbHelper = new CriadorDoBanco(ctx);
        dbHelperAberto = true;
    }

    private void abrirBanco() throws IllegalStateException {
        if(!isOpen()) {
            throw new IllegalStateException("Este repositório está fechado, isto é, seu método close" +
                    " já foi chamado. Esse fato impede a conexão com o banco de dados.");
        }
        //se o banco estiver fechado
        if((db == null) || !db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
    }

    private void fecharBanco() {
        //se o banco estiver aberto
        if(db != null) {
            db.close();
            db = null;
        }
    }

    public long inserir(DisciplinaTemUsuarioTemSemestreTemNota discTemUsuTemSemTemNota) {
        long disciplinaId = discTemUsuTemSemTemNota.getDisciplina().getId();
        if(disciplinaId <= 0) {
            RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
            disciplinaId = repDisc.inserir(discTemUsuTemSemTemNota.getDisciplina());
            repDisc.close();

            if(disciplinaId < 0) {
                return disciplinaId; //erro
            }
            discTemUsuTemSemTemNota.getDisciplina().setId(disciplinaId);
        }

        long usuarioId = discTemUsuTemSemTemNota.getUsuarioTemSemestre().getUsuario().getId();
        long semestreId = discTemUsuTemSemTemNota.getUsuarioTemSemestre().getSemestre().getId();
        if(usuarioId <= 0 || semestreId <= 0) {
            RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
            long retorno = repUsuTemSem.inserir(discTemUsuTemSemTemNota.getUsuarioTemSemestre());
            repUsuTemSem.close();
            if(retorno < 0) {
                return retorno; //erro
            }
        }

        long notaId = discTemUsuTemSemTemNota.getNota().getId();
        if(notaId <= 0) {
            RepositorioNota repNota = new RepositorioNota(ctx);
            notaId = repNota.inserir(discTemUsuTemSemTemNota.getNota());
            repNota.close();
            if(notaId < 0) {
                return notaId; //erro
            }
            discTemUsuTemSemTemNota.getNota().setId(notaId);
        }

        ContentValues valores = new ContentValues();
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID,
                discTemUsuTemSemTemNota.getDisciplina().getId());
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID,
                discTemUsuTemSemTemNota.getUsuarioTemSemestre().getUsuario().getId());
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID,
                discTemUsuTemSemTemNota.getUsuarioTemSemestre().getSemestre().getId());
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID,
                discTemUsuTemSemTemNota.getNota().getId());


        try {
            abrirBanco();

            long id = db.insert(DisciplinaTemUsuarioTemSemestreTemNota.NOME_DA_TABELA, "", valores);
            return id;
        } finally {
            fecharBanco();
        }
    }

    public long atualizar(DisciplinaTemUsuarioTemSemestreTemNota disciplinaTemUsuarioTemSemestreTemNotaAntigo,
                          DisciplinaTemUsuarioTemSemestreTemNota disciplinaTemUsuarioTemSemestreTemNotaNovo) {

        if(disciplinaTemUsuarioTemSemestreTemNotaAntigo.equals(disciplinaTemUsuarioTemSemestreTemNotaNovo)) {
            return 0;
        }

        ContentValues valores = new ContentValues();
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID,
                disciplinaTemUsuarioTemSemestreTemNotaNovo.getDisciplina().getId());
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID,
                disciplinaTemUsuarioTemSemestreTemNotaNovo.getUsuarioTemSemestre().getUsuario().getId());
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID,
                disciplinaTemUsuarioTemSemestreTemNotaNovo.getUsuarioTemSemestre().getSemestre().getId());
        valores.put(DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID,
                disciplinaTemUsuarioTemSemestreTemNotaNovo.getNota().getId());

        String idStrDisc = String.valueOf(disciplinaTemUsuarioTemSemestreTemNotaAntigo.getDisciplina().getId());
        String idStrUsu = String.valueOf(disciplinaTemUsuarioTemSemestreTemNotaAntigo.getUsuarioTemSemestre().getUsuario().getId());
        String idStrSem = String.valueOf(disciplinaTemUsuarioTemSemestreTemNotaAntigo.getUsuarioTemSemestre().getSemestre().getId());
        String idStrNota = String.valueOf(disciplinaTemUsuarioTemSemestreTemNotaAntigo.getNota().getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";

        String[] whereArgs = new String[]{idStrDisc, idStrUsu, idStrSem, idStrNota};

        try {
            abrirBanco();

            return db.update(DisciplinaTemUsuarioTemSemestreTemNota.NOME_DA_TABELA, valores, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(DisciplinaTemUsuarioTemSemestreTemNota disciplinaTemUsuarioTemSemestreTemNota) {
        return deletar(disciplinaTemUsuarioTemSemestreTemNota.getDisciplina().getId(),
                disciplinaTemUsuarioTemSemestreTemNota.getUsuarioTemSemestre().getUsuario().getId(),
                disciplinaTemUsuarioTemSemestreTemNota.getUsuarioTemSemestre().getSemestre().getId(),
                disciplinaTemUsuarioTemSemestreTemNota.getNota().getId());
    }

    public long deletar(long disciplinaId, long usuarioId, long semestreId, long notaId) {
        String idStrDisc = String.valueOf(disciplinaId);
        String idStrUsu = String.valueOf(usuarioId);
        String idStrSem = String.valueOf(semestreId);
        String idStrNota = String.valueOf(notaId);

        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";

        String[] whereArgs = new String[]{idStrDisc, idStrUsu, idStrSem, idStrNota};

        return deletar(where, whereArgs);
    }

    public long deletar(String where, String[] whereArgs) {
        try {
            abrirBanco();

            return db.delete(DisciplinaTemUsuarioTemSemestreTemNota.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    /**
     * falta terminar de implementar
     * @param disciplina
     * @param usuario
     * @param semestre
     * @param nota
     * @return
     */
    public long deletar(Disciplina disciplina, Usuario usuario, Semestre semestre, Nota nota) {
        if(disciplina == null && usuario == null && semestre == null && nota == null) {
            throw new NullPointerException("Todos os argumentos não podem ser nulos.");
        } else if(disciplina == null && usuario == null && semestre == null && nota != null) {

            return deletar(nota);
        } else if(disciplina == null && usuario == null && semestre != null && nota == null) {

            return deletar(semestre);
        } else if(disciplina == null && usuario == null && semestre != null && nota != null) {

            return deletar(semestre, nota);
        } else if(disciplina == null && usuario != null && semestre == null && nota == null) {

            return deletar(usuario);
        } else if(disciplina == null && usuario != null && semestre == null && nota != null) {

            return deletar(usuario, nota);
        } else if(disciplina == null && usuario != null && semestre != null && nota == null) {

            return deletar(new UsuarioTemSemestre(usuario, semestre));
        } else if(disciplina == null && usuario != null && semestre != null && nota != null) {

            return deletar(new UsuarioTemSemestre(usuario, semestre), nota);
        }
        else if(disciplina != null && usuario == null && semestre == null && nota == null) {

            return deletar(disciplina);
        } else if(disciplina != null && usuario == null && semestre == null && nota != null) {

            return deletar(disciplina, nota);
        } else if(disciplina != null && usuario == null && semestre != null && nota == null) {

            return deletar(disciplina, semestre);
        } else if(disciplina != null && usuario == null && semestre != null && nota != null) {

            return deletar(disciplina, semestre, nota);
        } else if(disciplina != null && usuario != null && semestre == null && nota == null) {

            return deletar(disciplina, usuario);
        } else if(disciplina != null && usuario != null && semestre == null && nota != null) {

            return deletar(disciplina, usuario, nota);
        } else if(disciplina != null && usuario != null && semestre != null && nota != null) {

            return deletar(disciplina.getId(), usuario.getId(), semestre.getId(), nota.getId());
        }
        return -1;
    }

    public long deletar(Disciplina porDisciplina) {
        String idStrDisc = String.valueOf(porDisciplina.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc};

        return deletar(where, whereArgs);
    }

    public long deletar(Usuario porUsuario) {
        String idStrUsu = String.valueOf(porUsuario.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu};

        return deletar(where, whereArgs);
    }

    public long deletar(Semestre porSemestre) {
        String idStrSem = String.valueOf(porSemestre.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrSem};

        return deletar(where, whereArgs);
    }

    public long deletar(Nota porNota) {
        String idStrNota = String.valueOf(porNota.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrNota};

        return deletar(where, whereArgs);
    }

    public long deletar(Disciplina porDisciplina, Usuario ePorUsuario) {
        String idStrDisc = String.valueOf(porDisciplina.getId());
        String idStrUsu = String.valueOf(ePorUsuario.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc, idStrUsu};

        return deletar(where, whereArgs);
    }

    public long deletar(Disciplina porDisciplina, Semestre ePorSemestre) {
        String idStrDisc = String.valueOf(porDisciplina.getId());
        String idStrSem = String.valueOf(ePorSemestre.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc, idStrSem};

        return deletar(where, whereArgs);
    }

    public long deletar(Disciplina porDisciplina, Nota ePorNota) {
        String idStrDisc = String.valueOf(porDisciplina.getId());
        String idStrNota = String.valueOf(ePorNota.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc, idStrNota};

        return deletar(where, whereArgs);
    }

    public long deletar(UsuarioTemSemestre porUsuarioTemSemestre) {
        String idStrUsu = String.valueOf(porUsuarioTemSemestre.getUsuario().getId());
        String idStrSem = String.valueOf(porUsuarioTemSemestre.getSemestre().getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ?"
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem};

        return deletar(where, whereArgs);
    }

    public long deletar(Usuario porUsuario, Nota porNota) {
        String idStrUsu = String.valueOf(porUsuario.getId());
        String idStrNota = String.valueOf(porNota.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrNota};

        return deletar(where, whereArgs);
    }

    public long deletar(Semestre porSemestre, Nota ePorNota) {
        String idStrSem = String.valueOf(porSemestre.getId());
        String idStrNota = String.valueOf(ePorNota.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrSem, idStrNota};

        return deletar(where, whereArgs);
    }

    public long deletar(Disciplina porDisciplina, UsuarioTemSemestre ePorUsuarioTemSemestre) {
        String idStrDisc = String.valueOf(porDisciplina.getId());
        String idStrUsu  = String.valueOf(ePorUsuarioTemSemestre.getUsuario().getId());
        String idStrSem  = String.valueOf(ePorUsuarioTemSemestre.getSemestre().getId());


        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc, idStrUsu, idStrSem};

        return deletar(where, whereArgs);
    }

    public long deletar(Disciplina porDisciplina, Usuario porUsuario, Nota ePorNota) {
        String idStrDisc = String.valueOf(porDisciplina.getId());
        String idStrUsu  = String.valueOf(porUsuario.getId());
        String idStrNota  = String.valueOf(ePorNota.getId());


        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc, idStrUsu, idStrNota};

        return deletar(where, whereArgs);
    }

    public long deletar(Disciplina porDisciplina, Semestre porSemestre, Nota ePorNota) {
        String idStrDisc = String.valueOf(porDisciplina.getId());
        String idStrSem  = String.valueOf(porSemestre.getId());
        String idStrNota  = String.valueOf(ePorNota.getId());


        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc, idStrSem, idStrNota};

        return deletar(where, whereArgs);
    }

    public long deletar(UsuarioTemSemestre porUsuarioTemSemestre, Nota ePorNota) {
        String idStrUsu = String.valueOf(porUsuarioTemSemestre.getUsuario().getId());
        String idStrSem = String.valueOf(porUsuarioTemSemestre.getSemestre().getId());
        String idStrNota = String.valueOf(ePorNota.getId());

        String where = DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ? AND "
                + DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem, idStrNota};

        return deletar(where, whereArgs);
    }

    public DisciplinaTemUsuarioTemSemestreTemNota buscar(long disciplinaId, long usuarioId, long semestreId, long notaId) {

        RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
        Disciplina disciplina = repDisc.buscar(disciplinaId);
        repDisc.close();
        if(disciplina == null) {
            return null;
        }

        RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
        UsuarioTemSemestre usuarioTemSemestre = repUsuTemSem.buscar(usuarioId, semestreId);
        repUsuTemSem.close();
        if(usuarioTemSemestre == null) {
            return null;
        }

        RepositorioNota repNota = new RepositorioNota(ctx);
        Nota nota = repNota.buscar(notaId);
        repNota.close();
        if(nota == null) {
            return null;
        }

        return new DisciplinaTemUsuarioTemSemestreTemNota(disciplina, usuarioTemSemestre, nota);
    }

    public Cursor getCursor() {
        try {
            abrirBanco();

            return db.query(DisciplinaTemUsuarioTemSemestreTemNota.NOME_DA_TABELA,
                    new String[] {DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID,
                            DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID,
                            DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID,
                            DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID},
                    null, null, null, null, null);
        } finally {
            fecharBanco();
        }
    }

    public List<DisciplinaTemUsuarioTemSemestreTemNota> listar() {
        Cursor c = getCursor();
        List<DisciplinaTemUsuarioTemSemestreTemNota> disciplinasTemUsuariosTemSemestresTemNotas
                = new ArrayList<DisciplinaTemUsuarioTemSemestreTemNota>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxDisciplinaId = c.getColumnIndex(DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID);
            int idxUsuarioId    = c.getColumnIndex(DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID);
            int idxSemestreId   = c.getColumnIndex(DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID);
            int idxNotaId       = c.getColumnIndex(DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID);

            RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
            RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
            RepositorioNota repNota = new RepositorioNota(ctx);

            //loop até o final
            do {
                DisciplinaTemUsuarioTemSemestreTemNota disciplinaTemUsuarioTemSemestreTemNota
                        = new DisciplinaTemUsuarioTemSemestreTemNota();

                disciplinaTemUsuarioTemSemestreTemNota.setDisciplina(repDisc.buscar(c.getLong(idxDisciplinaId)));
                disciplinaTemUsuarioTemSemestreTemNota.setUsuarioTemSemestre(repUsuTemSem
                        .buscar(c.getLong(idxUsuarioId), c.getLong(idxSemestreId)));
                disciplinaTemUsuarioTemSemestreTemNota.setNota(repNota.buscar(c.getLong(idxNotaId)));

                disciplinasTemUsuariosTemSemestresTemNotas.add(disciplinaTemUsuarioTemSemestreTemNota);

            } while(c.moveToNext());

            repDisc.close();
            repUsuTemSem.close();
            repNota.close();
            c.close();

            return disciplinasTemUsuariosTemSemestresTemNotas;
        }
        return null;
    }

    public List<DisciplinaTemUsuarioTemSemestreTemNota> listar(Disciplina porDisciplina) {
        List<DisciplinaTemUsuarioTemSemestreTemNota> disciplinasTemUsuariosTemSemestresTemNotas
                = new ArrayList<DisciplinaTemUsuarioTemSemestreTemNota>();

        String idStrDisc = String.valueOf(porDisciplina.getId());
        String where = DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID + " = ?";
        String[] whereArgs = new String[]{idStrDisc};

        abrirBanco();

        Cursor c = db.query(DisciplinaTemUsuarioTemSemestreTemNota.NOME_DA_TABELA,
                new String[] {DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID,
                        DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID,
                        DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID},
                where, whereArgs, null, null, null);

        fecharBanco();

        if(c.moveToFirst()) {
            RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
            RepositorioNota repNota = new RepositorioNota(ctx);

            do {
                UsuarioTemSemestre usuarioTemSemestre = repUsuTemSem.buscar(c.getLong(0), c.getLong(1));
                Nota nota = repNota.buscar(c.getLong(2));

                disciplinasTemUsuariosTemSemestresTemNotas.add(
                        new DisciplinaTemUsuarioTemSemestreTemNota(porDisciplina, usuarioTemSemestre, nota));
            } while(c.moveToNext());

            repUsuTemSem.close();
            repNota.close();
            c.close();

            return disciplinasTemUsuariosTemSemestresTemNotas;
        }
        return null;
    }

    public List<DisciplinaTemUsuarioTemSemestreTemNota> listar(UsuarioTemSemestre porUsuarioTemSemestre) {
        List<DisciplinaTemUsuarioTemSemestreTemNota> disciplinasTemUsuariosTemSemestresTemNotas
                = new ArrayList<DisciplinaTemUsuarioTemSemestreTemNota>();

        String idStrUsuTemSemUsu = String.valueOf(porUsuarioTemSemestre.getUsuario().getId());
        String idStrUsuTemSemSem = String.valueOf(porUsuarioTemSemestre.getSemestre().getId());
        String where = DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID + " = ?"
                + DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsuTemSemUsu, idStrUsuTemSemSem};

        abrirBanco();

        Cursor c = db.query(DisciplinaTemUsuarioTemSemestreTemNota.NOME_DA_TABELA,
                new String[] {DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID,
                        DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID},
                where, whereArgs, null, null, null);

        fecharBanco();

        if(c.moveToFirst()) {
            RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
            RepositorioNota repNota = new RepositorioNota(ctx);

            do {
                Disciplina disciplina = repDisc.buscar(c.getLong(0));
                Nota nota = repNota.buscar(c.getLong(1));

                disciplinasTemUsuariosTemSemestresTemNotas.add(
                        new DisciplinaTemUsuarioTemSemestreTemNota(disciplina, porUsuarioTemSemestre, nota));
            } while(c.moveToNext());

            repDisc.close();
            repNota.close();
            c.close();

            return disciplinasTemUsuariosTemSemestresTemNotas;
        }
        return null;
    }

    public List<DisciplinaTemUsuarioTemSemestreTemNota> listar(Nota porNota) {
        List<DisciplinaTemUsuarioTemSemestreTemNota> disciplinasTemUsuariosTemSemestresTemNotas
                = new ArrayList<DisciplinaTemUsuarioTemSemestreTemNota>();

        String idStrNota = String.valueOf(porNota.getId());
        String where = DisciplinaTemUsuarioTemSemestreTemNota.NOTA_ID + " = ?";
        String[] whereArgs = new String[]{idStrNota};

        abrirBanco();

        Cursor c = db.query(DisciplinaTemUsuarioTemSemestreTemNota.NOME_DA_TABELA,
                new String[] {DisciplinaTemUsuarioTemSemestreTemNota.DISCIPLINA_ID,
                        DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_USUARIO_ID,
                        DisciplinaTemUsuarioTemSemestreTemNota.USUARIO_HAS_SEMESTRE_SEMESTRE_ID},
                where, whereArgs, null, null, null);

        fecharBanco();

        if(c.moveToFirst()) {
            RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
            RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);

            do {
                Disciplina disciplina = repDisc.buscar(c.getLong(0));
                UsuarioTemSemestre usuarioTemSemestre = repUsuTemSem.buscar(c.getLong(1), c.getLong(2));

                disciplinasTemUsuariosTemSemestresTemNotas.add(
                        new DisciplinaTemUsuarioTemSemestreTemNota(disciplina, usuarioTemSemestre, porNota));
            } while(c.moveToNext());

            repDisc.close();
            repUsuTemSem.close();
            c.close();

            return disciplinasTemUsuariosTemSemestresTemNotas;
        }
        return null;
    }

    public List<DisciplinaTemUsuarioTemSemestreTemNota> listar(Disciplina porDisciplina,
            UsuarioTemSemestre usuarioTemSemestre) {

        List<DisciplinaTemUsuarioTemSemestreTemNota> disciplinasTemUsuariosTemSemestresTemNotas
                = new ArrayList<DisciplinaTemUsuarioTemSemestreTemNota>();

        disciplinasTemUsuariosTemSemestresTemNotas = listar(usuarioTemSemestre);

        long idDisc = porDisciplina.getId();

        long posicaoNaLista = 0;
        for (DisciplinaTemUsuarioTemSemestreTemNota discTemUsuTemSem : disciplinasTemUsuariosTemSemestresTemNotas) {

            if(discTemUsuTemSem.getDisciplina().getId() != idDisc) {
                disciplinasTemUsuariosTemSemestresTemNotas.remove(posicaoNaLista);
            }
            posicaoNaLista += 1;
        }

        return disciplinasTemUsuariosTemSemestresTemNotas;
    }

    public List<DisciplinaTemUsuarioTemSemestreTemNota> listar(Disciplina porDisciplina,
            Nota ePorNota) {

        List<DisciplinaTemUsuarioTemSemestreTemNota> disciplinasTemUsuariosTemSemestresTemNotas
                = new ArrayList<DisciplinaTemUsuarioTemSemestreTemNota>();

        disciplinasTemUsuariosTemSemestresTemNotas = listar(ePorNota);

        long idDisc = porDisciplina.getId();

        long posicaoNaLista = 0;
        for (DisciplinaTemUsuarioTemSemestreTemNota discTemUsuTemSem : disciplinasTemUsuariosTemSemestresTemNotas) {

            if(discTemUsuTemSem.getDisciplina().getId() != idDisc) {
                disciplinasTemUsuariosTemSemestresTemNotas.remove(posicaoNaLista);
            }
            posicaoNaLista += 1;
        }

        return disciplinasTemUsuariosTemSemestresTemNotas;
    }

    public List<DisciplinaTemUsuarioTemSemestreTemNota> listar(UsuarioTemSemestre porUsuarioTemSemestre,
            Nota ePorNota) {

        List<DisciplinaTemUsuarioTemSemestreTemNota> disciplinasTemUsuariosTemSemestresTemNotas
                = new ArrayList<DisciplinaTemUsuarioTemSemestreTemNota>();

        disciplinasTemUsuariosTemSemestresTemNotas = listar(porUsuarioTemSemestre);

        long idNota = ePorNota.getId();

        long posicaoNaLista = 0;
        for (DisciplinaTemUsuarioTemSemestreTemNota discTemUsuTemSem : disciplinasTemUsuariosTemSemestresTemNotas) {

            if(discTemUsuTemSem.getNota().getId() != idNota) {
                disciplinasTemUsuariosTemSemestresTemNotas.remove(posicaoNaLista);
            }
            posicaoNaLista += 1;
        }

        return disciplinasTemUsuariosTemSemestresTemNotas;
    }

    @Override
    public void close() {
        fecharBanco();
        if(dbHelper != null) {
            dbHelper.close();
        }
        dbHelperAberto = false;
    }

    public boolean isOpen() {
        return dbHelperAberto;
    }

}
