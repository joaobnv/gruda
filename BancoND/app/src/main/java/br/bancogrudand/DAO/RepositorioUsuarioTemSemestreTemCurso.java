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

import br.bancogrudand.entidades.Curso;
import br.bancogrudand.entidades.Semestre;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.entidades.UsuarioTemSemestre;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCurso;

/**
 * Created by JOÃO BRENO on 03/10/2016.
 */
public class RepositorioUsuarioTemSemestreTemCurso implements Closeable {

    public static final String CATEGORIA_LOG = "RepUsuTemSemTemCurso";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioUsuarioTemSemestreTemCurso(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public long inserir(UsuarioTemSemestreTemCurso usuTemSemTemCurso) {
        Log.i(CATEGORIA_LOG, "Inserindo.");

        long usuarioId = usuTemSemTemCurso.getUsuarioTemSemestre().getUsuario().getId();
        long semestreId = usuTemSemTemCurso.getUsuarioTemSemestre().getSemestre().getId();
        long cursoId = usuTemSemTemCurso.getCurso().getId();

        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID, usuarioId);
        valores.put(UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID, semestreId);
        valores.put(UsuarioTemSemestreTemCurso.CURSO_ID, cursoId);

        long retIns = db.insert(UsuarioTemSemestreTemCurso.NOME_DA_TABELA, "", valores);

        if(retIns < 0) { //erro
            Log.e(CATEGORIA_LOG, "Erro em inserir");
        }

        return retIns;
    }

    public long atualizar(UsuarioTemSemestreTemCurso uscAntigo, UsuarioTemSemestreTemCurso uscNovo) {
        Log.i(CATEGORIA_LOG, "Atualizando.");

        if(uscAntigo.equals(uscNovo)) {
            return 0;
        }

        RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN
                = new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(ctx);
        repUSCDN.deletar(uscAntigo.getUsuarioTemSemestre().getUsuario(), uscAntigo.getUsuarioTemSemestre().getSemestre(),
                uscAntigo.getCurso(), null, null);
        repUSCDN.close();

        //ids antigos
        String idStrUsu = String.valueOf(uscAntigo.getUsuarioTemSemestre().getUsuario().getId());
        String idStrSem = String.valueOf(uscAntigo.getUsuarioTemSemestre().getSemestre().getId());
        String idStrCurso = String.valueOf(uscAntigo.getCurso().getId());
        String where = UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID + " = ? AND " +
                UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID + " = ? AND "
                + UsuarioTemSemestreTemCurso.CURSO_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem, idStrCurso};

        //valores novos
        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID, uscNovo.getUsuarioTemSemestre().getUsuario().getId());
        valores.put(UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID, uscNovo.getUsuarioTemSemestre().getSemestre().getId());
        valores.put(UsuarioTemSemestreTemCurso.CURSO_ID, uscNovo.getCurso().getId());

        long qtdDeTuplasAtualizadas = db.update(UsuarioTemSemestreTemCurso.NOME_DA_TABELA, valores, where, whereArgs);

        Log.i(CATEGORIA_LOG, "Atualizou " + qtdDeTuplasAtualizadas + " tuplas");
        return qtdDeTuplasAtualizadas;
    }

    public long deletar(long usuarioId, long semestreId, long cursoId) {
        return deletar(new Usuario(usuarioId, null, null), new Semestre(semestreId, null), new Curso(cursoId, null));
    }

    public long deletar(UsuarioTemSemestreTemCurso usuTemSemTemCurso) {

        return deletar(usuTemSemTemCurso.getUsuarioTemSemestre().getUsuario(),
                usuTemSemTemCurso.getUsuarioTemSemestre().getSemestre(), usuTemSemTemCurso.getCurso());
    }

    public long deletar(Usuario porUsuario, Semestre porSemestre, Curso porCurso) {
        Log.i(CATEGORIA_LOG, "Deletando.");

        RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN =
                new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(ctx);
        repUSCDN.deletar(porUsuario, porSemestre, porCurso, null, null);
        repUSCDN.close();

        String where = "", log = "Deletando por ";
        if(porUsuario != null) {
            where += UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID + " = " + porUsuario.getId();
            log += "|usuario";
        }
        if(porCurso != null) {
            if(!where.equals("")) {
                where += " AND ";
            }
            where += UsuarioTemSemestreTemCurso.CURSO_ID + " = " + porCurso.getId();
            log += "|curso";
        }
        if(porSemestre != null) {
            if(!where.equals("")) {
                where += " AND ";
            }
            where += UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID + " = " + porSemestre.getId();
            log += "|semestre";
        }
        if(where.equals("")) {
            where = null;
            log = "Deletando tudo";
        }
        Log.i(CATEGORIA_LOG, log);

        long qtdDel = db.delete(UsuarioTemSemestreTemCurso.NOME_DA_TABELA, where, null);

        Log.i(CATEGORIA_LOG, "Deletou " + qtdDel + " tuplas.");
        return qtdDel;
    }

    public UsuarioTemSemestreTemCurso buscar(long usuarioId, long semestreId, long cursoId) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            String where = UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID + " = " + usuarioId +
                    " AND " + UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID + " = " + semestreId +
                    " AND " + UsuarioTemSemestreTemCurso.CURSO_ID + " = " + cursoId;

            Cursor c = db.query(UsuarioTemSemestreTemCurso.NOME_DA_TABELA,
                    new String[]{UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID},
                    where, null, null, null, null);

            if(c.moveToFirst()) { //se estiver no banco
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

                RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
                UsuarioTemSemestre usuTemSem = repUsuTemSem.buscar(usuarioId, semestreId);
                repUsuTemSem.close();
                if(usuTemSem == null) {
                    Log.w(CATEGORIA_LOG, "Não foi encontrado o UsuarioTemSemestre, portanto será retornado null.");
                    return null;
                }

                RepositorioCurso repCurso = new RepositorioCurso(ctx);
                Curso curso = repCurso.buscar(cursoId);
                repCurso.close();
                if(curso == null) {
                    Log.w(CATEGORIA_LOG, "Não foi encontrado o curso, portanto será retornado null.");
                    return null;
                }

                c.close();
                return new UsuarioTemSemestreTemCurso(usuTemSem, curso);
            }

            c.close();
            Log.w(CATEGORIA_LOG, "Buscando um dado que não está no banco.");

        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro em buscar.", ex);
        }

        return null; //se não estiver no banco
    }

    public Cursor getCursor() {
        Log.i(CATEGORIA_LOG, "Pegando o cursor.");
        try {
            return db.query(UsuarioTemSemestreTemCurso.NOME_DA_TABELA,
                    new String[]{UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID,
                            UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID, UsuarioTemSemestreTemCurso.CURSO_ID},
                    null, null, null, null, null);
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao buscar em getCursor.", ex);
            return null;
        }
    }

    public List<UsuarioTemSemestreTemCurso> listar(Usuario porUsuario, Semestre porSemestre, Curso porCurso) {
        Log.i(CATEGORIA_LOG, "Listando.");

        List<UsuarioTemSemestreTemCurso> lista = new ArrayList<UsuarioTemSemestreTemCurso>();
        try {
            String where = "", log = "Listando por ";
            if(porUsuario != null) {
                where += UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID + " = " + porUsuario.getId();
                log += "|usuario";
            }
            if(porSemestre != null) {
                if(!where.equals("")) {
                    where += " AND ";
                }
                where += UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID + " = " + porSemestre.getId();
                log += "|semestre";
            }
            if(porCurso != null) {
                if(!where.equals("")) {
                    where += " AND ";
                }
                where += UsuarioTemSemestreTemCurso.CURSO_ID + " = " + porCurso.getId();
                log += "|curso";
            }
            if(where.equals("")) {
                where = null;
                log += "Listando tudo.";
            }
            Log.i(CATEGORIA_LOG, log);

            Cursor c = db.query(UsuarioTemSemestreTemCurso.NOME_DA_TABELA,
                    new String[]{UsuarioTemSemestreTemCurso.USU_TEM_SEM_USUARIO_ID,
                            UsuarioTemSemestreTemCurso.USU_TEM_SEM_SEMESTRE_ID, UsuarioTemSemestreTemCurso.CURSO_ID},
                    where, null, null, null, null);

            if(c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado dados no banco.");

                RepositorioCurso repCurso = new RepositorioCurso(ctx);
                RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);

                do {
                    UsuarioTemSemestre usuTemSem = repUsuTemSem.buscar(c.getLong(0), c.getLong(1));
                    if(usuTemSem == null) {
                        Log.w(CATEGORIA_LOG, "Não foi encontrado o usuarioTemSemestre com o id que está na linha "
                                + c.getPosition() + " do cursor.");
                    }
                    Curso curso = repCurso.buscar(c.getLong(2));
                    if(curso == null) {
                        Log.w(CATEGORIA_LOG, "Não foi encontrado o curso com o id que está na linha "
                                + c.getPosition() + " do cursor.");
                    }

                    UsuarioTemSemestreTemCurso usuTemSemTemCurso = new UsuarioTemSemestreTemCurso(usuTemSem, curso);

                    lista.add(usuTemSemTemCurso);
                } while(c.moveToNext());

                repUsuTemSem.close();
                repCurso.close();
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
