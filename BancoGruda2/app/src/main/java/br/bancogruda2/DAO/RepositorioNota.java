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
import br.bancogruda2.entidades.Nota;

/**
 * Created by JOÃO BRENO on 31/08/2016.
 */
public class RepositorioNota implements Closeable {

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private boolean dbHelperAberto;

    public RepositorioNota(Context ctx) {
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

    public long inserir(Nota nota) {
        ContentValues valores = new ContentValues();
        valores.put(Nota.AV1, nota.getAv1());
        valores.put(Nota.AV2, nota.getAv2());

        try {
            abrirBanco();

            long id = db.insert(Nota.NOME_DA_TABELA, "", valores);
            return id;
        } finally {
            fecharBanco();
        }
    }

    public long atualizar(Nota nota) {
        ContentValues valores = new ContentValues();

        valores.put(Nota.AV1, nota.getAv1());
        valores.put(Nota.AV2, nota.getAv2());

        String idStr = String.valueOf(nota.getId());
        String where = Nota.ID + " = ?";
        String[] whereArgs = new String[]{idStr};

        try {
            abrirBanco();

            return db.update(Curso.NOME_DA_TABELA, valores, where, whereArgs);
        } finally {
            fecharBanco();
        }
    }

    public long deletar(Nota nota) {
        RepositorioDisciplinaTemUsuarioTemSemestreTemNota repDiscTemUsuTemSemTemNota
                = new RepositorioDisciplinaTemUsuarioTemSemestreTemNota(ctx);
        repDiscTemUsuTemSemTemNota.deletar(nota);
        repDiscTemUsuTemSemTemNota.close();

        String where = Nota.ID + " = ?";
        String idStr = String.valueOf(nota.getId());
        String whereArgs[] = new String[]{idStr};

        try {
            abrirBanco();

            return db.delete(Nota.NOME_DA_TABELA, where, whereArgs);
        } finally {
            fecharBanco();
        }

    }

    public long deletar(long id) {
        Nota nota = new Nota();
        nota.setId(id);
        return deletar(nota);
    }

    public Nota buscar(long id) {
        abrirBanco();

        Cursor c = db.query(Nota.NOME_DA_TABELA,
                new String[] {Nota.ID, Nota.AV1, Nota.AV2},
                Curso.ID + " = " + id, null, null, null, null);

        fecharBanco();

        if(c.getCount() > 0) {
            //posiciona no primeiro elemento do cursor
            c.moveToFirst();
            Nota nota = new Nota();
            //lê os dados
            nota.setId(c.getLong(0));
            nota.setAv1(c.getDouble(1));
            nota.setAv2(c.getDouble(2));

            c.close();

            return nota;
        }

        c.close();

        return null;
    }

    public Cursor getCursor() {
        try {
            abrirBanco();

            return db.query(Nota.NOME_DA_TABELA,
                    new String[] {Nota.ID, Nota.AV1, Nota.AV2},
                    null, null, null, null, null);
        } finally {
            fecharBanco();
        }
    }

    public List<Nota> listar() {
        Cursor c = getCursor();
        List<Nota> notas = new ArrayList<Nota>();

        if(c.moveToFirst()) {
            //recupera os índices das colunas
            int idxId = c.getColumnIndex(Nota.ID);
            int idxAv1 = c.getColumnIndex(Nota.AV1);
            int idxAv2 = c.getColumnIndex(Nota.AV2);
            //loop até o final
            do {
                Nota nota = new Nota();
                notas.add(nota);
                //recupera os atributos de nota
                nota.setId(c.getLong(idxId));
                nota.setAv1(c.getDouble(idxAv1));
                nota.setAv2(c.getDouble(idxAv2));
            } while(c.moveToNext());

            c.close();
            return notas;
        }

        c.close();
        return notas;
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
