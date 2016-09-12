package br.bancogruda2.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.bancogruda2.entidades.Curso;
import br.bancogruda2.entidades.Semestre;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
class CriadorDoBanco extends SQLiteOpenHelper {

    private static final String[] SCRIPT_DE_EXCLUSAO_DO_BANCO_DE_DADOS = new String[]{
            "DROP TABLE IF EXISTS usuario;",
            "DROP TABLE IF EXISTS semestre;",
            "DROP TABLE IF EXISTS " + Curso.NOME_DA_TABELA + "; ",
            "DROP TABLE IF EXISTS disciplina;",
            "DROP TABLE IF EXISTS nota;", //erro, foi colocado "notas", foi resolvido!
            "DROP TABLE IF EXISTS usuario_has_semestre;",
            "DROP TABLE IF EXISTS disciplina_has_usuario_has_semestre;"
    };

    private static final String[] SCRIPT_DE_CRIACAO_DO_BANCO_DE_DADOS = new String[]{
            "CREATE TABLE usuario( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome VARCHAR(100), " +
                    "linkFacebook VARCHAR(100) " + //erro, foi colocado "linkFacebbok", foi corrigido!
                    "); ",
            "CREATE TABLE " + Semestre.NOME_DA_TABELA + "( " +
                    Semestre.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Semestre.NUMERO + " VARCHAR(100) NOT NULL " + //trocar por integer
                    "); ",
            "CREATE TABLE " + Curso.NOME_DA_TABELA + "( " +
                    Curso.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Curso.DESCRICAO + " VARCHAR(100) NOT NULL " +
                    "); ",
            "CREATE TABLE disciplina( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao VARCHAR(100) NOT NULL, " +
                    "curso_id INTEGER, " +
                    "FOREIGN KEY(curso_id) REFERENCES curso(id) " + //erro, foi colocado "REFERENCES disciplina(id)", foi resolvido!
                    "); ",
            "CREATE TABLE nota( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "av1 DOUBLE, " +
                    "av2 D0UBLE " +
//                  "media_final DOUBLE, " + //retirar, deixar que a aplicação realize este calculo
//                  "situacao VARCHAR(100) " + //retirar, deixar que a aplicação realize este calculo
                    "); ",
            "CREATE TABLE usuario_has_semestre( " +
                    "usuario_id INTEGER NOT NULL, " +
                    "semestre_id INTEGER NOT NULL, " +
                    "PRIMARY KEY(usuario_id, semestre_id), " +
                    "FOREIGN KEY(usuario_id) REFERENCES usuario(id), " +
                    "FOREIGN KEY(semestre_id) REFERENCES semestre(id) " +
                    "); ",
            "CREATE TABLE disciplina_has_usuario_has_semestre( " +
                    "disciplina_id INTEGER NOT NULL, " +
                    "usuario_has_semestre_usuario_id INTEGER NOT NULL, " + //erro, faltava virgula, foi resolvido!
                    "usuario_has_semestre_semestre_id INTEGER NOT NULL, " + //erro, faltava virgula, foi resolvido!
                    "nota_id INTEGER NOT NULL, " +
                    "PRIMARY KEY(disciplina_id, usuario_has_semestre_usuario_id, " +
                        "usuario_has_semestre_semestre_id, nota_id), " + //erro, foi utilizado notas_id, foi resolvido!
                    "FOREIGN KEY(disciplina_id) REFERENCES disciplina(id), " +
                    "FOREIGN KEY(usuario_has_semestre_usuario_id, usuario_has_semestre_semestre_id)" +
                        "REFERENCES usuario_has_semestre(usuario_id, semestre_id)," +
//                  "FOREIGN KEY(usuario_has_semestre_usuario_id) REFERENCES usuario_has_semestre(usuario_id), " + //erro, chave estrangeira para chave
//                  "FOREIGN KEY(usuario_has_semestre_semestre_id) REFERENCES usuario_has_semestre(semestre_id), " + //primária composta, foi corrigido!
                    "FOREIGN KEY(nota_id) REFERENCES nota(id) " + //erro, colocado notas no lugar de nota, foi resolvido!
                    ");" //HOUVE UM ERRO POR FALTA DESSA SUBSTRING, FOI CORRIGIDO!
    };

    private static final String NOME_DO_BANCO_DE_DADOS = "appgruda";

    private static final int VERSAO_DO_BANCO_DE_DADOS = 1;

    public CriadorDoBanco(Context context) {
        super(context, NOME_DO_BANCO_DE_DADOS, null, VERSAO_DO_BANCO_DE_DADOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int qtdeScripts = SCRIPT_DE_CRIACAO_DO_BANCO_DE_DADOS.length;
        //executa cada SQL passado como parâmetro
        for(int i = 0; i < qtdeScripts; ++i) {
            String sql = SCRIPT_DE_CRIACAO_DO_BANCO_DE_DADOS[i];
            //cria o banco de dados executando o script de criação
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int qtdeScripts = SCRIPT_DE_EXCLUSAO_DO_BANCO_DE_DADOS.length;
        //deleta todas as tabelas do banco
        for (int i = 0; i < qtdeScripts; ++i) {
            db.execSQL(SCRIPT_DE_EXCLUSAO_DO_BANCO_DE_DADOS[i]);
        }
        //cria novamente o banco
        onCreate(db);
    }
}
