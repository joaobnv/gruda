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

import br.bancogrudand.entidades.Semestre;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.entidades.UsuarioTemSemestre;

/**
 * Created by JOÃO BRENO on 02/09/2016.
 */
public class RepositorioUsuarioTemSemestre implements Closeable {

    public static final String CATEGORIA_LOG = "RepositorioUsuarioTemSe";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioUsuarioTemSemestre(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public long inserir(UsuarioTemSemestre usuarioTemSemestre) {
        Log.i(CATEGORIA_LOG, "Inserindo.");

        long usuarioId = usuarioTemSemestre.getUsuario().getId();
        long semestreId = usuarioTemSemestre.getSemestre().getId();

        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestre.USUARIO_ID, usuarioId);
        valores.put(UsuarioTemSemestre.SEMESTRE_ID, semestreId);

        long retIns = db.insert(UsuarioTemSemestre.NOME_DA_TABELA, "", valores);

        if(retIns < 0) { //erro
            Log.e(CATEGORIA_LOG, "Erro em inserir");
        }

        return retIns;
    }

    public long atualizar(UsuarioTemSemestre usuTemSemAntigo, UsuarioTemSemestre usuTemSemNovo) {
        Log.i(CATEGORIA_LOG, "Atualizando.");

        if(usuTemSemAntigo.equals(usuTemSemNovo)) {
            return 0;
        }

        RepositorioUsuarioTemSemestreTemCurso repUSC
                = new RepositorioUsuarioTemSemestreTemCurso(ctx);
        repUSC.deletar(usuTemSemAntigo.getUsuario(), usuTemSemAntigo.getSemestre(), null);
        repUSC.close();

        String idStrUsu = String.valueOf( usuTemSemAntigo.getUsuario().getId() );
        String idStrSem = String.valueOf( usuTemSemAntigo.getSemestre().getId() );

        String where = UsuarioTemSemestre.USUARIO_ID + " = ? AND "
                + UsuarioTemSemestre.SEMESTRE_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem};

        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestre.USUARIO_ID, usuTemSemNovo.getUsuario().getId());
        valores.put(UsuarioTemSemestre.SEMESTRE_ID, usuTemSemNovo.getSemestre().getId());

        long qtdDeTuplasAtualizadas = db.update(UsuarioTemSemestre.NOME_DA_TABELA, valores, where, whereArgs);

        Log.i(CATEGORIA_LOG, "Atualizou " + qtdDeTuplasAtualizadas + " tuplas");
        return qtdDeTuplasAtualizadas;
    }

    public long deletar(long usuarioId, long semestreId) {
        return deletar(new Usuario(usuarioId, null, null), new Semestre(semestreId, null));
    }

    public long deletar(UsuarioTemSemestre usuarioTemSemestre) {
        return deletar(usuarioTemSemestre.getUsuario(), usuarioTemSemestre.getSemestre());
    }

    public long deletar(Usuario usuario, Semestre semestre) {
        Log.i(CATEGORIA_LOG, "Deletando.");

        RepositorioUsuarioTemSemestreTemCurso repUSC
                = new RepositorioUsuarioTemSemestreTemCurso(ctx);
        repUSC.deletar(usuario, semestre, null);
        repUSC.close();

        String where = "", log = "Deletando por ";
        if(usuario != null) {
            where += UsuarioTemSemestre.USUARIO_ID + " = " + usuario.getId();
            log += "|usuario";
        }
        if(semestre != null) {
            if(!where.equals("")) {
                where += " AND ";
            }
            where += UsuarioTemSemestre.SEMESTRE_ID + " = " + semestre.getId();
            log += "|semestre";
        }
        if(where.equals("")) {
            where = null;
            log = "Deletando tudo.";
        }
        Log.i(CATEGORIA_LOG, log);

        long qtdDel = db.delete(UsuarioTemSemestre.NOME_DA_TABELA, where, null);

        Log.i(CATEGORIA_LOG, "Deletou " + qtdDel + " tuplas.");
        return qtdDel;
    }

    public UsuarioTemSemestre buscar(long usuarioId, long semestreId) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            String where = UsuarioTemSemestre.USUARIO_ID + " = " + usuarioId + " AND " +
                    UsuarioTemSemestre.SEMESTRE_ID + " = " + semestreId;

            Cursor c = db.query(UsuarioTemSemestre.NOME_DA_TABELA, new String[]{UsuarioTemSemestre.USUARIO_ID},
                    where, null, null, null, null);

            if(c.moveToFirst()) { //se estiver no banco
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

                RepositorioUsuario repUsu = new RepositorioUsuario(ctx);
                Usuario usuario = repUsu.buscar(usuarioId);
                repUsu.close();
                if(usuario == null) {
                    Log.w(CATEGORIA_LOG, "Não foi encontrado o usuario, portanto será retornado null.");
                    return null;
                }

                RepositorioSemestre repSem = new RepositorioSemestre(ctx);
                Semestre semestre = repSem.buscar(semestreId);
                repSem.close();
                if(semestre == null) {
                    Log.w(CATEGORIA_LOG, "Não foi encontrado o semestre, portanto será retornado null.");
                    return null;
                }

                c.close();
                return new UsuarioTemSemestre(usuario, semestre);
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
            return db.query(UsuarioTemSemestre.NOME_DA_TABELA,
                    new String[] {UsuarioTemSemestre.USUARIO_ID, UsuarioTemSemestre.SEMESTRE_ID},
                    null, null, null, null, null);
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao buscar em getCursor.", ex);
            return null;
        }
    }

    public List<UsuarioTemSemestre> listar(Usuario porUsuario, Semestre porSemestre) {
        Log.i(CATEGORIA_LOG, "Listando.");

        List<UsuarioTemSemestre> lista = new ArrayList<UsuarioTemSemestre>();
        try {
            String where = "", log = "Listando por ";
            if(porUsuario != null) {
                where += UsuarioTemSemestre.USUARIO_ID + " = " + porUsuario.getId();
                log += "|usuario";
            }
            if(porSemestre != null) {
                if(!where.equals("")) {
                    where += " AND ";
                }
                where += UsuarioTemSemestre.SEMESTRE_ID + " = " + porSemestre.getId();
                log += "|semestre";
            }
            if(where.equals("")) {
                where = null;
                log = "Listando tudo.";
            }
            Log.i(CATEGORIA_LOG, log);

            Cursor c = db.query(UsuarioTemSemestre.NOME_DA_TABELA,
                    new String[]{UsuarioTemSemestre.USUARIO_ID, UsuarioTemSemestre.SEMESTRE_ID},
                    where, null, null, null, null);

            if (c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado dados no banco.");

                RepositorioUsuario repUsu = new RepositorioUsuario(ctx);
                RepositorioSemestre repSem = new RepositorioSemestre(ctx);

                do {
                    Usuario usuario = repUsu.buscar(c.getLong(0));
                    if(usuario == null) {
                        Log.w(CATEGORIA_LOG, "Não foi encontrado o usuario com o id que está na linha "
                                + c.getPosition() + " do cursor.");
                    }
                    Semestre semestre = repSem.buscar(c.getLong(1));
                    if(semestre == null) {
                        Log.w(CATEGORIA_LOG, "Não foi encontrado o semestre com o id que está na linha "
                                + c.getPosition() + " do cursor.");
                    }
                    UsuarioTemSemestre usuarioTemSemestre = new UsuarioTemSemestre(usuario, semestre);

                    lista.add(usuarioTemSemestre);
                } while (c.moveToNext());

                repUsu.close();
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
