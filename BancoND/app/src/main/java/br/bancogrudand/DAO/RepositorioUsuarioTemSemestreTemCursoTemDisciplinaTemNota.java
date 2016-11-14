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
import br.bancogrudand.entidades.Disciplina;
import br.bancogrudand.entidades.Nota;
import br.bancogrudand.entidades.Semestre;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.entidades.UsuarioTemSemestre;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCurso;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCursoTemDisciplinaTemNota;

/**
 * Created by JOÃO BRENO on 03/10/2016.
 */
public class RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota implements Closeable {

    public static final String CATEGORIA_LOG = "RepUsuSemCursoDiscNota";

    private Context ctx;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(Context ctx) {
        dbHelper = new CriadorDoBanco(ctx);
        db = dbHelper.getWritableDatabase();
        this.ctx = ctx;
        Log.i(CATEGORIA_LOG, "Repositório criado.");
    }

    public long inserir(UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn) {
        Log.i(CATEGORIA_LOG, "Inserindo.");

        long usuarioId = uscdn.getUsuTemSemTemCurso().getUsuarioTemSemestre().getUsuario().getId();
        long semestreId = uscdn.getUsuTemSemTemCurso().getUsuarioTemSemestre().getSemestre().getId();
        long disciplinaId = uscdn.getDisciplina().getId();
        long cursoId = uscdn.getDisciplina().getCurso().getId();

        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID, usuarioId);
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID, semestreId);
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID, disciplinaId);
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID, cursoId);
        if(uscdn.getNota() != null) { //nota pode ser null
            valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID, uscdn.getNota().getId());
            Log.i(CATEGORIA_LOG, "Nota não nula em inserir.");
        } else {
            valores.putNull(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID);
            Log.i(CATEGORIA_LOG, "Nota nula em inserir.");
        }

        long retIns = db.insert(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOME_DA_TABELA, "", valores);

        if(retIns < 0) { //erro
            Log.e(CATEGORIA_LOG, "Erro em inserir");
        }

        return retIns;
    }

    public long atualizar(UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdnAntigo,
                          UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdnNovo) {
        Log.i(CATEGORIA_LOG, "Atualizando.");

        if(uscdnAntigo.equals(uscdnNovo)) {
            Log.i(CATEGORIA_LOG, "Só precisa atualizar a nota.");
            atualizarNota(uscdnNovo);
            return 0;
        }

        //ids antigos
        String idStrUsu        = String.valueOf( uscdnAntigo.getUsuTemSemTemCurso().getUsuarioTemSemestre().getUsuario().getId() );
        String idStrSem        = String.valueOf( uscdnAntigo.getUsuTemSemTemCurso().getUsuarioTemSemestre().getSemestre().getId() );
        String idStrCurso      = String.valueOf( uscdnAntigo.getUsuTemSemTemCurso().getCurso().getId() );
        String idStrDisciplina = String.valueOf( uscdnAntigo.getDisciplina().getId() );

        String where = UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID + " = ? AND " +
                UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID + " = ? AND " +
                UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID + " = ? AND " +
                UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem, idStrCurso, idStrDisciplina};

        //valores novos
        ContentValues valores = new ContentValues();
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID,
                uscdnNovo.getUsuTemSemTemCurso().getUsuarioTemSemestre().getUsuario().getId());
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID,
                uscdnNovo.getUsuTemSemTemCurso().getUsuarioTemSemestre().getSemestre().getId());
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID, uscdnNovo.getUsuTemSemTemCurso().getCurso().getId());
        valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID, uscdnNovo.getDisciplina().getId());
        if(uscdnNovo.getNota() != null) {
            valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID, uscdnNovo.getNota().getId());
            Log.i(CATEGORIA_LOG, "Nota não nula em atualizar.");
        } else {
            valores.putNull(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID);
            Log.i(CATEGORIA_LOG, "Nota nula em atualizar.");
        }

        long qtdDeTuplasAtualizadas = db.update(UsuarioTemSemestre.NOME_DA_TABELA, valores, where, whereArgs);

        Log.i(CATEGORIA_LOG, "Atualizou " + qtdDeTuplasAtualizadas + " tuplas");
        return qtdDeTuplasAtualizadas;
    }

    public long atualizarNota(UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn) {
        Log.i(CATEGORIA_LOG, "Atualizando nota.");

        String idStrUsu = String.valueOf( uscdn.getUsuTemSemTemCurso().getUsuarioTemSemestre().getUsuario().getId() );
        String idStrSem = String.valueOf( uscdn.getUsuTemSemTemCurso().getUsuarioTemSemestre().getSemestre().getId() );
        String idStrCurso = String.valueOf( uscdn.getUsuTemSemTemCurso().getCurso().getId() );
        String idStrDisciplina = String.valueOf( uscdn.getDisciplina().getId() );

        String where = UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID + " = ? AND " +
                UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID + " = ? AND " +
                UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID + " = ? AND " +
                UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID + " = ?";
        String[] whereArgs = new String[]{idStrUsu, idStrSem, idStrCurso, idStrDisciplina};

        ContentValues valores = new ContentValues();
        if(uscdn.getNota() != null) {
            valores.put(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID, uscdn.getNota().getId());
            Log.i(CATEGORIA_LOG, "Nota não nula em atualizar nota.");
        } else {
            valores.putNull(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID);
            Log.i(CATEGORIA_LOG, "Nota nula em atualizar nota.");
        }

        long qtdDeTuplasAtualizadas = db.update(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOME_DA_TABELA,
                valores, where, whereArgs);

        Log.i(CATEGORIA_LOG, "Atualizou " + qtdDeTuplasAtualizadas + " tuplas");
        return qtdDeTuplasAtualizadas;
    }

    public long deletar(long usuarioId, long semestreId, long cursoId, long disciplinaId) {

        Usuario usuario = new Usuario(usuarioId, null, null);
        Semestre semestre = new Semestre(semestreId, null);
        Curso curso = new Curso(cursoId, null);
        Disciplina disciplina = new Disciplina(disciplinaId, null, null);

        return deletar(usuario, semestre, curso, disciplina, null);
    }

    public long deletar(UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn) {

        return deletar(uscdn.getUsuTemSemTemCurso().getUsuarioTemSemestre().getUsuario(),
                uscdn.getUsuTemSemTemCurso().getUsuarioTemSemestre().getSemestre(),
                uscdn.getUsuTemSemTemCurso().getCurso(), uscdn.getDisciplina(), uscdn.getNota());
    }

    public long deletar(Usuario porUsuario, Semestre porSemestre, Curso porCurso, Disciplina porDisciplina, Nota porNota) {
        Log.i(CATEGORIA_LOG, "Deletando.");

        String where = "", log = "Deletando por ";
        if(porUsuario != null) {
            where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID + " = " + porUsuario.getId();
            log += "|usuario";
        }
        if(porSemestre != null) {
            if(!where.equals("")) {
                where += " AND ";
            }
            where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID + " = " + porSemestre.getId();
            log += "|semestre";
        }
        if(porCurso != null) {
            if(!where.equals("")) {
                where += " AND ";
            }
            where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID + " = " + porCurso.getId();
            log += "|curso";
        }
        if(porDisciplina != null) {
            if(!where.equals("")) {
                where += " AND ";
            }
            where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID + " = " + porDisciplina.getId();
            log += "|disciplina";
        }
        if(porNota != null) {
            if(!where.equals("")) {
                where += " AND ";
            }
            where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID + " = " + porNota.getId();
            log += "|nota";
        }
        if(where.equals("")) {
            where = null;
            log = "Deletando tudo.";
        }
        Log.i(CATEGORIA_LOG, log);

        long qtdDel = db.delete(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOME_DA_TABELA, where, null);

        Log.i(CATEGORIA_LOG, "Deletou " + qtdDel + " tuplas.");
        return qtdDel;
    }

    public UsuarioTemSemestreTemCursoTemDisciplinaTemNota buscar(long usuarioId, long semestreId, long cursoId, long disciplinaId) {
        Log.i(CATEGORIA_LOG, "Buscando 1.");

        try {
            String where = UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID + " = " + usuarioId +
                    " AND " + UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID + " = " + semestreId +
                    " AND " + UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID + " = " + cursoId +
                    " AND " + UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID + " = " + disciplinaId;

            Cursor c = db.query(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOME_DA_TABELA, new String[]{
                    UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID}, where, null, null, null, null);

            if(c.moveToFirst()) { //se estiver no banco
                Log.i(CATEGORIA_LOG, "Foi encontrado um no banco.");

                RepositorioUsuarioTemSemestreTemCurso repUSC = new RepositorioUsuarioTemSemestreTemCurso(ctx);
                UsuarioTemSemestreTemCurso usc = repUSC.buscar(usuarioId, semestreId, cursoId);
                repUSC.close();
                if(usc == null) {
                    Log.w(CATEGORIA_LOG, "Não foi encontrado o UsuarioTemSemestreTemNota, portanto será retornado null.");
                    return null;
                }

                RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
                Disciplina disciplina = repDisc.buscar(disciplinaId);
                repDisc.close();
                if(disciplina == null) {
                    Log.w(CATEGORIA_LOG, "Não foi encontrada a disciplina, portanto será retornado null.");
                    return null;
                }

                Nota nota = null;
                if(!c.isNull(0)) { //se o campo com o id da nota não tiver um valor null
                    Log.i(CATEGORIA_LOG, "A nota não é nula");

                    Long notaId = c.getLong(0);
                    RepositorioNota repNota = new RepositorioNota(ctx);
                    nota = repNota.buscar(notaId);
                    repNota.close();
                }

                c.close();
                return new UsuarioTemSemestreTemCursoTemDisciplinaTemNota(usc, disciplina, nota);
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

            return db.query(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOME_DA_TABELA,
                    new String[]{UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID},
                    null, null, null, null, null);
        } catch(Exception ex) {
            Log.e(CATEGORIA_LOG, "Erro ao buscar em getCursor.", ex);
            return null;
        }
    }

    public List<UsuarioTemSemestreTemCursoTemDisciplinaTemNota> listar(Usuario porUsuario, Semestre porSemestre,
        Curso porCurso, Disciplina porDisciplina, Nota porNota) {
        Log.i(CATEGORIA_LOG, "Listando.");

        List<UsuarioTemSemestreTemCursoTemDisciplinaTemNota> lista = new ArrayList<UsuarioTemSemestreTemCursoTemDisciplinaTemNota>();
        try {
            String where = "", log = "Listando por ";
            if(porUsuario != null) {
                where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID +
                        " = " + porUsuario.getId();
                log += "|usuario";
            }
            if(porSemestre != null) {
                if(!where.equals("")) {
                    where += " AND ";
                }
                where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID +
                        " = " + porSemestre.getId();
                log += "|semestre";
            }
            if(porCurso != null) {
                if(!where.equals("")) {
                    where += " AND ";
                }
                where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID +
                        " = " + porCurso.getId();
                log += "|curso";
            }
            if(porDisciplina != null) {
                if(!where.equals("")) {
                    where += " AND ";
                }
                where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID + " = " + porDisciplina.getId();
                log += "|disciplina";
            }
            if(porNota != null) {
                if(!where.equals("")) {
                    where += " AND ";
                }
                where += UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID + " = " + porNota.getId();
                log += "|nota";
            }
            if(where.equals("")) {
                where = null;
                log = "Listando tudo.";
            }
            Log.i(CATEGORIA_LOG, log);

            Cursor c = db.query(UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOME_DA_TABELA,
                    new String[]{UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_USUARIO_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_USU_TEM_SEM_SEMESTRE_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.USU_TEM_SEM_TEM_CURSO_CURSO_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.DISCIPLINA_ID,
                            UsuarioTemSemestreTemCursoTemDisciplinaTemNota.NOTA_ID},
                    where, null, null, null, null);

            if(c.moveToFirst()) {
                Log.i(CATEGORIA_LOG, "Foi encontrado dados no banco.");

                RepositorioUsuarioTemSemestreTemCurso repUSC = new RepositorioUsuarioTemSemestreTemCurso(ctx);
                RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
                RepositorioNota repNota = new RepositorioNota(ctx);

                do {
                    UsuarioTemSemestreTemCurso usc = repUSC.buscar(c.getLong(0), c.getLong(1), c.getLong(2));
                    if(usc == null) {
                        Log.w(CATEGORIA_LOG, "Não foi encontrado o UsuarioTemSemestreTemNota com o id que está na linha "
                                + c.getPosition() + " do cursor.");
                    }
                    Disciplina disc = repDisc.buscar(c.getLong(3));
                    if(usc == null) {
                        Log.w(CATEGORIA_LOG, "Não foi encontrada a disciplina com o id que está na linha "
                                + c.getPosition() + " do cursor.");
                    }
                    Nota nota = null;
                    if(!c.isNull(4)) { //se o valor do id da nota não for null
                        nota = repNota.buscar(c.getLong(4));
                        Log.i(CATEGORIA_LOG, "A nota é nula.");
                    }

                    UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn =
                            new UsuarioTemSemestreTemCursoTemDisciplinaTemNota(usc, disc, nota);

                    lista.add(uscdn);
                } while(c.moveToNext());

                repUSC.close();
                repDisc.close();
                repNota.close();
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
