package br.bancogruda2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import br.bancogruda2.entidades.Curso;
import br.bancogruda2.entidades.Disciplina;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class RepositorioCurso implements Closeable {

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private boolean dbHelperAberto;

    public RepositorioCurso(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        dbHelperAberto = true;
        this.ctx = ctx;
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

    public long inserir(Curso curso) {
        ContentValues valores = new ContentValues();
        valores.put(Curso.DESCRICAO, curso.getDescricao());

        try {
            abrirBanco();

            long id = db.insert(Curso.NOME_DA_TABELA, "", valores);
            return id;
        } finally {
            fecharBanco();
        }
    }

    public long atualizar(Curso curso) {
        ContentValues valores = new ContentValues();
        valores.put(Curso.DESCRICAO, curso.getDescricao());

        String idStr = String.valueOf(curso.getId());
        String where = Curso.ID + " = ?";
        String[] whereArgs = new String[]{idStr};

        abrirBanco();

        long qtdDeTuplasAtualizadas = db.update(Curso.NOME_DA_TABELA, valores, where, whereArgs);

        fecharBanco();

        return qtdDeTuplasAtualizadas;
    }

    public long deletar(Curso curso) {
        RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
        repDisc.deletar(curso);
        repDisc.close();

        String where = Curso.ID + " = ?";
        String idStr = String.valueOf(curso.getId());
        String whereArgs[] = new String[]{idStr};

        try {
            abrirBanco();

            return db.delete(Curso.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }

    }

    public long deletar(long id) {
        Curso curso = new Curso();
        curso.setId(id);
        return deletar(curso);
    }

    public Curso buscar(long id) {
        abrirBanco();

        Cursor c = db.query(Curso.NOME_DA_TABELA, new String[] {Curso.ID, Curso.DESCRICAO},
                Curso.ID + " = " + id, null, null, null, null);

        fecharBanco();

        if(c.getCount() > 0) {
            //posiciona no primeiro elemento do cursor
            c.moveToFirst();
            Curso curso = new Curso();
            //lê os dados
            curso.setId(c.getLong(0));
            curso.setDescricao(c.getString(1));

            c.close();

            return curso;
        }

        c.close();

        return null;
    }

    public Cursor getCursor() {
        try {
            abrirBanco();

            return db.query(Curso.NOME_DA_TABELA, new String[] {Curso.ID, Curso.DESCRICAO},
                    null, null, null, null, null);
        } finally {
            fecharBanco();
        }
    }

    public List<Curso> listar() {
        Cursor c = getCursor();
        List<Curso> cursos = new ArrayList<Curso>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxId = c.getColumnIndex(Curso.ID);
            int idxDescricao = c.getColumnIndex(Curso.DESCRICAO);
            //loop até o final
            do {
                Curso curso = new Curso();
                cursos.add(curso);
                //recupera os atributos de curso
                curso.setId(c.getLong(idxId));
                curso.setDescricao(c.getString(idxDescricao));
            } while(c.moveToNext());

            c.close();
            return cursos;
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
