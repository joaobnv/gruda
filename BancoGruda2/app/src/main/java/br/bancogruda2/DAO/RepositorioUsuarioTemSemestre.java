package br.bancogruda2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.bancogruda2.entidades.UsuarioTemSemestre;
import br.bancogruda2.entidades.Usuario;
import br.bancogruda2.entidades.Semestre;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JOÃO BRENO on 02/09/2016.
 */
public class RepositorioUsuarioTemSemestre implements Closeable {

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private boolean dbHelperAberto;

    public RepositorioUsuarioTemSemestre(Context ctx) {
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

    public long inserir(UsuarioTemSemestre usuarioTemSemestre) {
        long usuarioId = usuarioTemSemestre.getUsuario().getId();
        if(usuarioId <= 0) {
            RepositorioUsuario repUsu = new RepositorioUsuario(ctx);
            usuarioId = repUsu.inserir(usuarioTemSemestre.getUsuario());
            repUsu.close();
            if(usuarioId < 0) {
                return usuarioId; //erro
            }
            usuarioTemSemestre.getUsuario().setId(usuarioId);
        }

        long semestreId = usuarioTemSemestre.getSemestre().getId();
        if(semestreId <= 0) {
            RepositorioSemestre repSem = new RepositorioSemestre(ctx);
            semestreId = repSem.inserir(usuarioTemSemestre.getSemestre());
            repSem.close();
            if(semestreId < 0) {
                return semestreId; //erro
            }
            usuarioTemSemestre.getSemestre().setId(semestreId);
        }

        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestre.USUARIO_ID, usuarioId);
        valores.put(UsuarioTemSemestre.SEMESTRE_ID, semestreId);

        try {
            abrirBanco();

            return db.insert(UsuarioTemSemestre.NOME_DA_TABELA, "", valores);
        } finally {
            fecharBanco();
        }
    }

    public long atualizar(UsuarioTemSemestre usuarioTemSemestreAntigo, UsuarioTemSemestre usuarioTemSemestreNovo) {

        if(usuarioTemSemestreAntigo.equals(usuarioTemSemestreNovo)) {
            return 0;
        }

        RepositorioDisciplinaTemUsuarioTemSemestreTemNota repDiscTemUsuTemSemTemNota
                = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        repDiscTemUsuTemSemTemNota.deletar(usuarioTemSemestreAntigo);
        repDiscTemUsuTemSemTemNota.close();

        String idStrUsu = String.valueOf(usuarioTemSemestreAntigo.getUsuario().getId());
        String idStrSem = String.valueOf(usuarioTemSemestreAntigo.getSemestre().getId());
        String where = UsuarioTemSemestre.USUARIO_ID + " = ? AND "
                + UsuarioTemSemestre.SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem};

        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestre.USUARIO_ID, usuarioTemSemestreNovo.getUsuario().getId());
        valores.put(UsuarioTemSemestre.SEMESTRE_ID, usuarioTemSemestreNovo.getSemestre().getId());

        try {
            abrirBanco();

            return db.update(UsuarioTemSemestre.NOME_DA_TABELA, valores, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(long usuario_id, long semestre_id) {
        return deletar(new UsuarioTemSemestre(new Usuario(usuario_id, null, null),
                new Semestre(semestre_id, 0)));
    }

    public long deletar(UsuarioTemSemestre usuarioTemSemestre) {
        RepositorioDisciplinaTemUsuarioTemSemestreTemNota repDiscTemUsuTemSemTemNota
                = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        repDiscTemUsuTemSemTemNota.deletar(usuarioTemSemestre);
        repDiscTemUsuTemSemTemNota.close();

        String idStrUsu = String.valueOf(usuarioTemSemestre.getUsuario().getId());
        String idStrSem = String.valueOf(usuarioTemSemestre.getUsuario().getId());
        String where = UsuarioTemSemestre.USUARIO_ID + " = ? AND "
                + UsuarioTemSemestre.SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem};

        try {
            abrirBanco();

            return db.delete(UsuarioTemSemestre.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(Usuario porUsuario) {
        RepositorioDisciplinaTemUsuarioTemSemestreTemNota repDiscTemUsuTemSemTemNota
                = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        repDiscTemUsuTemSemTemNota.deletar(porUsuario);
        repDiscTemUsuTemSemTemNota.close();


        String idStrUsu = String.valueOf(porUsuario.getId());
        String where = UsuarioTemSemestre.USUARIO_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu};

        try {
            abrirBanco();

            return db.delete(UsuarioTemSemestre.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(Semestre porSemestre) {
        RepositorioDisciplinaTemUsuarioTemSemestreTemNota repDiscTemUsuTemSemTemNota
                = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        repDiscTemUsuTemSemTemNota.deletar(porSemestre);
        repDiscTemUsuTemSemTemNota.close();

        String idStrSem = String.valueOf(porSemestre.getId());
        String where = UsuarioTemSemestre.SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrSem};

        try {
            abrirBanco();

            return db.delete(UsuarioTemSemestre.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public UsuarioTemSemestre buscar(long usuarioId, long semestreId) {

        RepositorioUsuario repUsu = new RepositorioUsuario(ctx);
        Usuario usuario = repUsu.buscar(usuarioId);
        repUsu.close();
        if(usuario == null) {
            return null;
        }

        RepositorioSemestre repSem = new RepositorioSemestre(ctx);
        Semestre semestre = repSem.buscar(semestreId);
        repSem.close();
        if(semestre == null) {
            return null;
        }

        return new UsuarioTemSemestre(usuario, semestre);
    }

    public Cursor getCursor() {
        try {
            abrirBanco();

            return db.query(UsuarioTemSemestre.NOME_DA_TABELA,
                    new String[] {UsuarioTemSemestre.USUARIO_ID, UsuarioTemSemestre.SEMESTRE_ID},
                    null, null, null, null, null);
        } finally {
            fecharBanco();
        }
    }

    public List<UsuarioTemSemestre> listar() {
        Cursor c = getCursor();
        List<UsuarioTemSemestre> usuariosTemSemestres = new ArrayList<UsuarioTemSemestre>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxUsuarioId = c.getColumnIndex(UsuarioTemSemestre.USUARIO_ID);
            int idxSemestreId = c.getColumnIndex(UsuarioTemSemestre.SEMESTRE_ID);

            RepositorioUsuario repUsu = new RepositorioUsuario(ctx);
            RepositorioSemestre repSem = new RepositorioSemestre(ctx);

            //loop até o final
            do {
                UsuarioTemSemestre usuarioTemSemestre = new UsuarioTemSemestre();

                usuarioTemSemestre.setUsuario(repUsu.buscar(c.getLong(idxUsuarioId)));
                usuarioTemSemestre.setSemestre(repSem.buscar(c.getLong(idxSemestreId)));

                usuariosTemSemestres.add(usuarioTemSemestre);
            } while(c.moveToNext());

            repUsu.close();
            repSem.close();
            c.close();

            return usuariosTemSemestres;
        }
        return null;
    }

    public List<UsuarioTemSemestre> listar(Usuario porUsuario) {
        List<UsuarioTemSemestre> usuariosTemSemestres = new ArrayList<UsuarioTemSemestre>();

        String idStrUsu = String.valueOf(porUsuario.getId());
        String where = UsuarioTemSemestre.USUARIO_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu};

        abrirBanco();

        Cursor c = db.query(UsuarioTemSemestre.NOME_DA_TABELA,
                new String[] {UsuarioTemSemestre.SEMESTRE_ID},
                where, whereArgs, null, null, null);

        fecharBanco();

        if(c.moveToFirst()) {
            RepositorioSemestre repSem = new RepositorioSemestre(ctx);

            do {
                Semestre semestre = repSem.buscar(c.getLong(0));

                UsuarioTemSemestre usuarioTemSemestre = new UsuarioTemSemestre(porUsuario, semestre);

                usuariosTemSemestres.add(usuarioTemSemestre);
            } while(c.moveToNext());

            repSem.close();
            c.close();

            return usuariosTemSemestres;
        }
        return null;
    }

    public List<UsuarioTemSemestre> listar(Semestre porSemestre) {
        List<UsuarioTemSemestre> usuariosTemSemestres = new ArrayList<UsuarioTemSemestre>();

        String idStrSem = String.valueOf(porSemestre.getId());
        String where = UsuarioTemSemestre.SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrSem};

        abrirBanco();

        Cursor c = db.query(UsuarioTemSemestre.NOME_DA_TABELA,
                new String[] {UsuarioTemSemestre.USUARIO_ID},
                where, whereArgs, null, null, null);

        fecharBanco();

        if(c.moveToFirst()) {
            RepositorioUsuario repUsu = new RepositorioUsuario(ctx);

            do {
                Usuario usuario = repUsu.buscar(c.getLong(0));

                UsuarioTemSemestre usuarioTemSemestre = new UsuarioTemSemestre(usuario, porSemestre);

                usuariosTemSemestres.add(usuarioTemSemestre);
            } while(c.moveToNext());

            repUsu.close();
            c.close();

            return usuariosTemSemestres;
        }
        return null;
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
