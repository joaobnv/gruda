package br.bancogrudand.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Closeable;
import java.util.List;

import br.bancogrudand.entidades.Curso;
import br.bancogrudand.entidades.Nota;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCursoTemDisciplinaTemNota;

/**
 * Created by JOÃO BRENO on 31/08/2016.
 */
public class RepositorioNota implements Closeable {


    private static final String CATEGORIA_LOG = "RepositorioNota";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioNota(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public long inserir(Nota nota) {
        Log.i(CATEGORIA_LOG, "Inserindo.");

        ContentValues valores = getValues(nota);

        long id = db.insert(Nota.NOME_DA_TABELA, "", valores);

        if(id < 0) { //erro
            Log.e(CATEGORIA_LOG, "Erro em inserir");
        }

        return id;
    }

    public long atualizar(Nota nota) {
        Log.i(CATEGORIA_LOG, "Atualizando.");

        ContentValues valores = getValues(nota);

        String idStr = String.valueOf(nota.getId());
        String where = Nota.ID + " = ?";
        String[] whereArgs = new String[]{idStr};

        long qtdDeTuplasAtualizadas = db.update(Nota.NOME_DA_TABELA, valores, where, whereArgs);

        Log.i(CATEGORIA_LOG, "Atualizou " + qtdDeTuplasAtualizadas + " tuplas");
        return qtdDeTuplasAtualizadas;
    }

    public long deletar(Nota nota) {
        Log.i(CATEGORIA_LOG, "Deletando.");

        List<UsuarioTemSemestreTemCursoTemDisciplinaTemNota> listaUSCDN;

        RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN
                = new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(ctx);
        listaUSCDN = repUSCDN.listar(null, null, null, null, nota);

        if(listaUSCDN.size() > 0) {
            for(UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn : listaUSCDN) {
                uscdn.setNota(null);
                repUSCDN.atualizarNota(uscdn);
                Log.i(CATEGORIA_LOG, "Um usuário dependia dessa nota, portanto agora ela é nula para ele.");
            }
        }

        repUSCDN.close();

        String where, log;
        if(nota != null) {
            where = Nota.ID + " = " + nota.getId();
            log = "Deletando por |nota";
        } else {
            where = null;
            log = "Deletando tudo";
        }
        Log.i(CATEGORIA_LOG, log);

        long qtdDel = db.delete(Nota.NOME_DA_TABELA, where, null);

        Log.i(CATEGORIA_LOG, "Deletou " + qtdDel + " tuplas.");
        return qtdDel;
    }

    public long deletar(long id) {
        return deletar(new Nota(id, null, null, null, null, null));
    }

    public Nota buscar(long id) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            Cursor c = db.query(Nota.NOME_DA_TABELA,
                    new String[]{Nota.ID, Nota.AV1, Nota.AV2, Nota.MEDIA, Nota.AVF, Nota.MEDIA_FINAL},
                    Curso.ID + " = " + id, null, null, null, null);

            if(c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

                //posiciona no primeiro elemento do cursor
                Nota nota = new Nota();
                //lê os dados
                if(!c.isNull(0)) {
                    nota.setId(c.getLong(0));
                }
                if(!c.isNull(1)) {
                    nota.setAv1(c.getDouble(1));
                }
                if(!c.isNull(2)) {
                    nota.setAv2(c.getDouble(2));
                }
                if(!c.isNull(3)) {
                    nota.setMedia(c.getDouble(3));
                }
                if(!c.isNull(4)) {
                    nota.setAvf(c.getDouble(4));
                }
                if(!c.isNull(5)) {
                    nota.setMediaFinal(c.getDouble(5));
                }

                c.close();
                return nota;
            }

            c.close();
            Log.w(CATEGORIA_LOG, "Buscando um dado que não está no banco.");

        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro em buscar.", ex);
        }
        return null;
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

    private ContentValues getValues(Nota nota) {
        Double av1 = nota.getAv1();
        Double av2 = nota.getAv2();
        Double media = nota.getMedia();
        Double avf = nota.getAvf();
        Double mediaFinal = nota.getMediaFinal();

        ContentValues valores = new ContentValues();
        if(av1 != null) {
            valores.put(Nota.AV1, av1);
        } else {
            valores.putNull(Nota.AV1);
        }
        if(av2 != null) {
            valores.put(Nota.AV2, av2);
        } else {
            valores.putNull(Nota.AV2);
        }
        if(media != null) {
            valores.put(Nota.MEDIA, media);
        } else {
            valores.putNull(Nota.MEDIA);
        }
        if(avf != null) {
            valores.put(Nota.AVF, avf);
        } else {
            valores.putNull(Nota.AVF);
        }
        if(mediaFinal != null) {
            valores.put(Nota.MEDIA_FINAL, mediaFinal);
        } else {
            valores.putNull(Nota.MEDIA_FINAL);
        }

        return valores;
    }

}
