package br.bancogruda2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import br.bancogruda2.entidades.Semestre;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
public class RepositorioSemestre implements Closeable {

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private boolean dbHelperAberto;

    public RepositorioSemestre(Context ctx) {
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

    public long inserir(Semestre semestre) {
        ContentValues valores = new ContentValues();
        valores.put(Semestre.NUMERO, semestre.getNumero());

        try {
            abrirBanco();

            long id = db.insert(Semestre.NOME_DA_TABELA, "", valores);
            return id;
        } finally {
            fecharBanco();
        }
    }

    public long atualizar(Semestre semestre) {
        ContentValues valores = new ContentValues();
        valores.put(Semestre.NUMERO, semestre.getNumero());
        String idStr = String.valueOf(semestre.getId());
        String where = Semestre.ID + " = ?";
        String[] whereArgs = {idStr};

        try {
            abrirBanco();

            return db.update(Semestre.NOME_DA_TABELA, valores, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(Semestre semestre) {
        RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
        repUsuTemSem.deletar(semestre);
        repUsuTemSem.close();

        String where = Semestre.ID + " = ?";
        String idStr = String.valueOf(semestre.getId());
        String whereArgs[] = new String[]{idStr};

        try {
            abrirBanco();
            return db.delete(Semestre.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(long id) {
        Semestre semestre = new Semestre();
        semestre.setId(id);

        return deletar(semestre);
    }

    public Semestre buscar(long id) {
        abrirBanco();

        Cursor c = db.query(Semestre.NOME_DA_TABELA, new String[] {Semestre.ID, Semestre.NUMERO},
                Semestre.ID + " = " + id, null, null, null, null);

        fecharBanco();

        if(c.getCount() > 0) {
            //posiciona no primeiro elemento do cursor
            c.moveToFirst();
            Semestre semestre = new Semestre();
            //lê os dados
            semestre.setId(c.getLong(0));
            semestre.setNumero(c.getLong(1));

            c.close();

            return semestre;
        }

        c.close();

        return null;
    }

    public Cursor getCursor() {
        try {
            abrirBanco();

            return db.query(Semestre.NOME_DA_TABELA, new String[] {Semestre.ID, Semestre.NUMERO},
                    null, null, null, null, null);
        } finally {
            fecharBanco();
        }
    }

    public List<Semestre> listar() {
        Cursor c = getCursor();
        List<Semestre> semestres = new ArrayList<Semestre>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxId = c.getColumnIndex(Semestre.ID);
            int idxDescricao = c.getColumnIndex(Semestre.NUMERO);
            //loop até o final
            do {
                Semestre semestre = new Semestre();
                semestres.add(semestre);
                //recupera os atributos de semestre
                semestre.setId(c.getLong(idxId));
                semestre.setNumero(c.getLong(idxDescricao));
            } while(c.moveToNext());

            c.close();
            return semestres;
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
