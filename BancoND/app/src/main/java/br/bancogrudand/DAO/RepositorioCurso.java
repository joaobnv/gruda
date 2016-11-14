package br.bancogrudand.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import br.bancogrudand.entidades.Curso;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class RepositorioCurso implements Closeable {

    private static final String CATEGORIA_LOG = "RepositorioCurso";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioCurso(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public Curso buscar(long id) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            Cursor c = db.query(Curso.NOME_DA_TABELA, new String[]{Curso.ID, Curso.DESCRICAO},
                    Curso.ID + " = " + id, null, null, null, null);

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

                try {
                    return new Curso(c.getLong(0), c.getString(1));
                }finally {
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
            return db.query(Curso.NOME_DA_TABELA, new String[] {Curso.ID, Curso.DESCRICAO},
                    null, null, null, null, Curso.DESCRICAO); //order by descricao
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao buscar em getCursor.", ex);
            return null;
        }
    }

    public List<Curso> listar() {
        Log.i(CATEGORIA_LOG, "Listando.");

        List<Curso> lista = new ArrayList<Curso>();
        try {
            Cursor c = getCursor();

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado dados no banco.");

                //recupera os índices das colunas
                int idxId = c.getColumnIndex(Curso.ID);
                int idxDescricao = c.getColumnIndex(Curso.DESCRICAO);
                //loop até o final
                do {
                    Curso curso = new Curso(c.getLong(idxId), c.getString(idxDescricao));
                    lista.add(curso);
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
