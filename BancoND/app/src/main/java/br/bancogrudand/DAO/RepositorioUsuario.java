package br.bancogrudand.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import br.bancogrudand.entidades.Usuario;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class RepositorioUsuario implements Closeable {

    public static final String CATEGORIA_LOG = "RepositorioUsuario";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioUsuario(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public long inserir(Usuario usuario) {
        Log.i(CATEGORIA_LOG, "Inserindo.");

        ContentValues valores = new ContentValues();
        valores.put(Usuario.NOME, usuario.getNome());
        valores.put(Usuario.LINK_FACEBOOK, usuario.getLinkFacebook());

        long id = db.insert(Usuario.NOME_DA_TABELA, "", valores);

        if(id < 0) { //erro
            Log.e(CATEGORIA_LOG, "Erro em inserir");
        }

        return id;
    }

    public long atualizar(Usuario usuario) {
        Log.i(CATEGORIA_LOG, "Atualizando.");

        ContentValues valores = new ContentValues();
        valores.put(Usuario.NOME, usuario.getNome());
        valores.put(Usuario.LINK_FACEBOOK, usuario.getLinkFacebook());

        String idStr = String.valueOf(usuario.getId());
        String where = Usuario.ID + " = ?";
        String[] whereArgs = new String[]{idStr};

        long qtdDeTuplasAtualizadas = db.update(Usuario.NOME_DA_TABELA, valores, where, whereArgs);

        Log.i(CATEGORIA_LOG, "Atualizou " + qtdDeTuplasAtualizadas + " tuplas");
        return qtdDeTuplasAtualizadas;
    }

    public long deletar(Usuario usuario) {
        Log.i(CATEGORIA_LOG, "Deletando.");

        RepositorioUsuarioTemSemestre repUsuTemSem =
                new RepositorioUsuarioTemSemestre(ctx);
        repUsuTemSem.deletar(usuario, null);
        repUsuTemSem.close();

        String where = "", log;
        if(usuario != null) {
            where += Usuario.ID + " = " + usuario.getId();
            log = "Deletando por |usuario";
        } else {
            where = null;
            log = "Deletando tudo";
        }

        long qtdDel = db.delete(Usuario.NOME_DA_TABELA, where, null);

        Log.i(CATEGORIA_LOG, "Deletou " + qtdDel + "tuplas.");
        return qtdDel;
    }

    public long deletar(long id) {
        return deletar(new Usuario(id, null, null));
    }

    public Usuario buscar(long id) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            Cursor c = db.query(Usuario.NOME_DA_TABELA, new String[]{Usuario.NOME,
                    Usuario.LINK_FACEBOOK}, Usuario.ID + " = " + id, null, null, null, null);

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

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
            Log.w(CATEGORIA_LOG, "Buscando um dado que não está no banco.");

        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro em buscar.", ex);
        }
        return null;
    }

    public Cursor getCursor() {
        Log.i(CATEGORIA_LOG, "Pegando o cursor.");
        try {
            return db.query(Usuario.NOME_DA_TABELA, new String[] {Usuario.ID, Usuario.NOME, Usuario.LINK_FACEBOOK},
                    null, null, null, null, Usuario.NOME); //order by nome
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao buscar em getCursor: ", ex);
            return null;
        }
    }

    public List<Usuario> listar() {
        Log.i(CATEGORIA_LOG, "Listando.");

        List<Usuario> lista = new ArrayList<Usuario>();
        try {
            Cursor c = getCursor();

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado dados no banco.");

                //recupera os índices das colunas
                int idxId = c.getColumnIndex(Usuario.ID);
                int idxNome = c.getColumnIndex(Usuario.NOME);
                int idxLinkFacebook = c.getColumnIndex(Usuario.LINK_FACEBOOK);
                //loop até o final
                do {
                    Usuario usuario = new Usuario();
                    usuario.setId(c.getLong(idxId));
                    usuario.setNome(c.getString(idxNome));
                    usuario.setLinkFacebook(c.getString(idxLinkFacebook));

                    lista.add(usuario);
                } while (c.moveToNext());
            }
            c.close();
        }catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao listar: "+ ex.toString());
        }

        Log.i(CATEGORIA_LOG, "Quantidade de tuplas listadas: " + lista.size());
        return lista;
    }

    @Override
    public void close() {
        if(db != null) {
            if(!db.isOpen()) {
                db.close();
                db = null;
            }
        }
        if(dbHelper != null) {
            dbHelper.close();
        }
        Log.i(CATEGORIA_LOG, "Repositório fechado.");
    }

}
