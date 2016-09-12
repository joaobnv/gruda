package br.bancogruda2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import br.bancogruda2.entidades.Usuario;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class RepositorioUsuario implements Closeable {

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private boolean dbHelperAberto;

    public RepositorioUsuario(Context ctx) {
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

    public long inserir(Usuario usuario) {



        ContentValues valores = new ContentValues();
        valores.put(Usuario.NOME, usuario.getNome());
        valores.put(Usuario.LINK_FACEBOOK, usuario.getLinkFacebook());

        abrirBanco();

        long id = db.insert(Usuario.NOME_DA_TABELA, "", valores);

        fecharBanco();

        usuario.setId(id);

        return id;
    }

    public long atualizar(Usuario usuario) {
        ContentValues valores = new ContentValues();
        valores.put(Usuario.NOME, usuario.getNome());

        String idStr = String.valueOf(usuario.getId());
        String where = Usuario.ID + " = ?";
        String[] whereArgs = new String[]{idStr};

        try {
            abrirBanco();

            return db.update(Usuario.NOME_DA_TABELA, valores, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);

        return deletar(usuario);
    }

    public long deletar(Usuario usuario) {
        RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
        repUsuTemSem.deletar(usuario);
        repUsuTemSem.close();

        String where = Usuario.ID + " = ?";
        String idStr = String.valueOf(usuario.getId());
        String whereArgs[] = new String[]{idStr};

        try {
            abrirBanco();
            return db.delete(Usuario.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public Usuario buscar(long id) {
        abrirBanco();

        Cursor c = db.query(Usuario.NOME_DA_TABELA, new String[] {Usuario.NOME,
                Usuario.LINK_FACEBOOK}, Usuario.ID + " = " + id, null, null, null, null);

        fecharBanco();

        if(c.moveToFirst()) {
            //posiciona no primeiro elemento do cursor
            Usuario usuario = new Usuario();
            //lê os dados
            usuario.setId(id);
            usuario.setNome(c.getString(0));
            usuario.setLinkFacebook(c.getString(1));

            c.close();

            return usuario;
        }

        c.close();
        return null;
    }

    public Cursor getCursor() {
        try {
            abrirBanco();
            return db.query(Usuario.NOME_DA_TABELA, new String[] {Usuario.ID, Usuario.NOME, Usuario.LINK_FACEBOOK},
                    null, null, null, null, null);
        } finally {
            fecharBanco();
        }
    }

    public List<Usuario> listar() {
        Cursor c = getCursor();
        List<Usuario> usuarios = new ArrayList<Usuario>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxId           = c.getColumnIndex(Usuario.ID);
            int idxNome         = c.getColumnIndex(Usuario.NOME);
            int idxLinkFacebook = c.getColumnIndex(Usuario.LINK_FACEBOOK);
            //loop até o final
            do {
                Usuario usuario = new Usuario();
                usuario.setId(c.getLong(idxId));
                usuario.setNome(c.getString(idxNome));
                usuario.setLinkFacebook(c.getString(idxLinkFacebook));

                usuarios.add(usuario);
            } while(c.moveToNext());

            c.close();
            return usuarios;
        }

        c.close();
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
