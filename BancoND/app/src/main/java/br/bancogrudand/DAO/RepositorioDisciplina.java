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
import br.bancogrudand.entidades.Disciplina;

/**
 * Created by JOÃO BRENO on 01/09/2016.
 */
public class RepositorioDisciplina implements Closeable {

    private static final String CATEGORIA_LOG = "RepositorioDisciplina";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioDisciplina(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public Disciplina buscar(long id) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            Cursor c = db.query(Disciplina.NOME_DA_TABELA,
                    new String[]{Disciplina.ID, Disciplina.DESCRICAO, Disciplina.CURSO_ID},
                    Disciplina.ID + " = " + id, null, null, null, null);

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

                RepositorioCurso repCurso = new RepositorioCurso(ctx);
                Curso curso = repCurso.buscar(c.getLong(2));
                repCurso.close();
                if (curso == null) {
                    Log.w(CATEGORIA_LOG, "Não foi encontrado o curso, portanto será retornado null.");
                    return null;
                }

                try {
                    return new Disciplina(c.getLong(0), c.getString(1), curso);
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

            return db.query(Disciplina.NOME_DA_TABELA,
                    new String[] {Disciplina.ID, Disciplina.DESCRICAO, Disciplina.CURSO_ID},
                    null, null, null, null, Disciplina.DESCRICAO); //order by descricao
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao buscar em getCursor.", ex);
            return null;
        }
    }

    public List<Disciplina> listar(Curso porCurso) {
        Log.i(CATEGORIA_LOG, "Listando.");

        List<Disciplina> lista = new ArrayList<Disciplina>();
        try {
            String where = "", log = "Listando por ";
            if(porCurso != null) {
                where += Disciplina.CURSO_ID + " = " + porCurso.getId();
                log += "|curso";
            }
            if(where.equals("")) {
                where = null;
                log = "Listando tudo.";
            }
            Log.i(CATEGORIA_LOG, log);

            Cursor c = db.query(Disciplina.NOME_DA_TABELA,
                    new String[]{Disciplina.ID, Disciplina.DESCRICAO, Disciplina.CURSO_ID},
                    where, null, null, null, Disciplina.DESCRICAO); //order by descricao

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado dados no banco.");

                RepositorioCurso repCurso = new RepositorioCurso(ctx);
                //loop até o final
                do {
                    Curso curso = repCurso.buscar(c.getLong(2));

                    if(curso == null) {
                        Log.w(CATEGORIA_LOG, "Não foi encontrado o curso com o id que está na linha "
                                + c.getPosition() + " do cursor.");
                    }

                    Disciplina disciplina = new Disciplina(c.getLong(0), c.getString(1), curso);

                    lista.add(disciplina);
                } while (c.moveToNext());

                repCurso.close();
            }
            c.close();
        }  catch(Exception ex) {
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
