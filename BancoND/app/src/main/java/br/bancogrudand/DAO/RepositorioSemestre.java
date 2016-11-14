package br.bancogrudand.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import br.bancogrudand.entidades.Semestre;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class RepositorioSemestre implements Closeable {

    private static final String CATEGORIA_LOG = "RepositorioSemestre";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioSemestre(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public Semestre buscar(long id) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            Cursor c = db.query(Semestre.NOME_DA_TABELA, new String[]{Semestre.ID, Semestre.DESCRICAO},
                    Semestre.ID + " = " + id, null, null, null, null);

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

                try {
                    return new Semestre(c.getLong(0), c.getString(1));
                } finally {
                    c.close();
                }
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
            return db.query(Semestre.NOME_DA_TABELA, new String[] {Semestre.ID, Semestre.DESCRICAO},
                    null, null, null, null, Semestre.DESCRICAO); //order by numero
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao buscar em getCursor.", ex);
            return null;
        }
    }

    public List<Semestre> listar() {
        Log.i(CATEGORIA_LOG, "Listando.");

        List<Semestre> lista = new ArrayList<Semestre>();
        try {
            Cursor c = getCursor();

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado dados no banco.");

                //recupera os índices das colunas
                int idxId = c.getColumnIndex(Semestre.ID);
                int idxDescricao = c.getColumnIndex(Semestre.DESCRICAO);
                //loop até o final
                do {

                    Semestre semestre = new Semestre(c.getLong(idxId), c.getString(idxDescricao));
                    lista.add(semestre);

                } while (c.moveToNext());
            }

            c.close();
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao listar.", ex);
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
