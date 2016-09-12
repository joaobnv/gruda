package br.bancogruda2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import br.bancogruda2.entidades.Curso;
import br.bancogruda2.entidades.Disciplina;

/**
 * Created by JOÃO BRENO on 01/09/2016.
 */
public class RepositorioDisciplina implements Closeable {

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private boolean dbHelperAberto;

    public RepositorioDisciplina(Context ctx) {
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

    public long inserir(Disciplina disciplina) {
        long cursoId = disciplina.getCurso().getId();

        if(cursoId <= 0) {
            RepositorioCurso repCurso = new RepositorioCurso(ctx);
            cursoId = repCurso.inserir(disciplina.getCurso());
            repCurso.close();
            if(cursoId <= 0) {
                return cursoId; //erro
            }
            disciplina.getCurso().setId(cursoId);
        }

        ContentValues valores = new ContentValues();
        valores.put(Disciplina.DESCRICAO, disciplina.getDescricao());
        valores.put(Disciplina.CURSO_ID, cursoId);

        try {
            abrirBanco();

            long id = db.insert(Disciplina.NOME_DA_TABELA, "", valores);
            return id;
        } finally {
            fecharBanco();
        }
    }

    public long atualizar(Disciplina disciplina) {
        ContentValues valores = new ContentValues();
        valores.put(Disciplina.DESCRICAO, disciplina.getDescricao());
        valores.put(Disciplina.CURSO_ID, disciplina.getCurso().getId());

        String idStr = String.valueOf(disciplina.getId());
        String where = Disciplina.ID + " = ?";
        String[] whereArgs = new String[]{idStr};

        try {
            abrirBanco();

            return db.update(Disciplina.NOME_DA_TABELA, valores, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(Disciplina disciplina) {
        RepositorioDisciplinaTemUsuarioTemSemestreTemNota repDiscTemUsuTemSemTemNota
                = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        repDiscTemUsuTemSemTemNota.deletar(disciplina);
        repDiscTemUsuTemSemTemNota.close();

        String where = Disciplina.ID + " = ?";
        String idStr = String.valueOf(disciplina.getId());
        String whereArgs[] = new String[]{idStr};

        try {
            abrirBanco();

            return db.delete(Disciplina.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(Curso porCurso) {
        List<Disciplina> disciplinasPorOCurso = listar(porCurso);
        int numberOfRowsAffected = 0;
        for(Disciplina disc : disciplinasPorOCurso) {
            numberOfRowsAffected += deletar(disc);
        }
        return numberOfRowsAffected;
    }

    public long deletar(long id) {
        Disciplina disciplina = new Disciplina();
        disciplina.setId(id);
        return deletar(disciplina);
    }

    public Disciplina buscar(long id) {
        abrirBanco();

        Cursor c = db.query(Disciplina.NOME_DA_TABELA,
                new String[] {Disciplina.ID, Disciplina.DESCRICAO, Disciplina.CURSO_ID},
                Disciplina.ID + " = " + id, null, null, null, null);

        fecharBanco();

        if(c.getCount() > 0) {
            //posiciona no primeiro elemento do cursor
            c.moveToFirst();
            Disciplina disciplina = new Disciplina();
            //lê os dados
            disciplina.setId(c.getLong(0));
            disciplina.setDescricao(c.getString(1));

            RepositorioCurso repCurso = new RepositorioCurso(ctx);
            Curso curso = repCurso.buscar(c.getLong(2));
            repCurso.close();

            disciplina.setCurso(curso);

            c.close();

            return disciplina;
        }

        c.close();

        return null;
    }

    public Cursor getCursor() {
        try {
            abrirBanco();

            return db.query(Disciplina.NOME_DA_TABELA,
                    new String[] {Disciplina.ID, Disciplina.DESCRICAO, Disciplina.CURSO_ID},
                    null, null, null, null, null);
        } finally {
            fecharBanco();
        }
    }

    public List<Disciplina> listar() {
        Cursor c = getCursor();
        List<Disciplina> disciplinas = new ArrayList<Disciplina>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxId = c.getColumnIndex(Disciplina.ID);
            int idxDescricao = c.getColumnIndex(Disciplina.DESCRICAO);
            int idxCursoId = c.getColumnIndex(Disciplina.CURSO_ID);

            RepositorioCurso repCurso = new RepositorioCurso(ctx);

            //loop até o final
            do {
                Disciplina disciplina = new Disciplina();
                disciplinas.add(disciplina);
                //recupera os atributos de disciplina
                disciplina.setId(c.getLong(idxId));
                disciplina.setDescricao(c.getString(idxDescricao));

                Curso curso = repCurso.buscar(c.getLong(idxCursoId));
                disciplina.setCurso(curso);
            } while(c.moveToNext());

            repCurso.close();
            c.close();
            return disciplinas;
        }

        c.close();
        return null;
    }

    public List<Disciplina> listar(Curso porCurso) {
        abrirBanco();
        Cursor c = db.query(Disciplina.NOME_DA_TABELA,
                new String[] {Disciplina.ID, Disciplina.DESCRICAO},
                Disciplina.CURSO_ID + " = " + porCurso.getId(), null, null, null, null);
        fecharBanco();

        List<Disciplina> disciplinas = new ArrayList<Disciplina>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxId = c.getColumnIndex(Disciplina.ID);
            int idxDescricao = c.getColumnIndex(Disciplina.DESCRICAO);

            //loop até o final
            do {
                Disciplina disciplina = new Disciplina();
                disciplinas.add(disciplina);
                //recupera os atributos de disciplina
                disciplina.setId(c.getLong(idxId));
                disciplina.setDescricao(c.getString(idxDescricao));
                disciplina.setCurso(porCurso);
            } while(c.moveToNext());

            c.close();
            return disciplinas;
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
