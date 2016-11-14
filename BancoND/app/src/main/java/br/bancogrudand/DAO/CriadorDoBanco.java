package br.bancogrudand.DAO;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by JOÃO BRENO on 29/08/2016.
 */
class CriadorDoBanco extends SQLiteOpenHelper {

    private static final String CATEGORIA_LOG = "CriadorDoBanco";

    private static final String[] SCRIPT_DE_DELECAO_DO_BANCO_DE_DADOS = new String[]{
            "DROP TABLE IF EXISTS usuario_tem_semestre_tem_curso_tem_disciplina_tem_nota;",
            "DROP TABLE IF EXISTS usuario_tem_semestre_tem_curso;",
            "DROP TABLE IF EXISTS usuario_tem_semestre;",
            "DROP TABLE IF EXISTS disciplina;",
            "DROP TABLE IF EXISTS curso;",
            "DROP TABLE IF EXISTS nota;",
            "DROP TABLE IF EXISTS usuario;",
            "DROP TABLE IF EXISTS semestre;"
    };

    private static final String[] SCRIPT_DE_CRIACAO_DO_BANCO_DE_DADOS = new String[]{
            "CREATE TABLE usuario( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "linkfacebook VARCHAR(255) UNIQUE" +
                    "); ",
            "CREATE TABLE semestre( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao VARCHAR(10) NOT NULL UNIQUE" +
                    "); ",
            "CREATE TABLE curso( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao VARCHAR(100) NOT NULL UNIQUE" +
                    "); ",
            "CREATE TABLE disciplina ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao VARCHAR(100) NOT NULL, " +
                    "curso_id INTEGER NOT NULL, " +
                    /*"FOREIGN KEY(curso_id) REFERENCES curso(id) ON DELETE CASCADE," +*/
                    "CONSTRAINT uk_descricao_e_curso_id UNIQUE(descricao, curso_id)" +
                    "); ",
            "CREATE TABLE nota( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "av1 DOUBLE, " +
                    "av2 D0UBLE, " +
                    "media DOUBLE, " +
                    "avf DOUBLE, " +
                    "media_final DOUBLE " +
                    "); ",
            "CREATE TABLE usuario_tem_semestre( " +
                    "usu_id INTEGER NOT NULL, " +
                    "sem_id INTEGER NOT NULL, " +
                    "CONSTRAINT pk_composta PRIMARY KEY(usu_id, sem_id) " +
                    /*"FOREIGN KEY(usu_id) REFERENCES usuario(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(sem_id) REFERENCES semestre(id) ON DELETE CASCADE" +*/
                    "); ",
            "CREATE TABLE usuario_tem_semestre_tem_curso( " +
                    "usu_t_sem_usu_id INTEGER NOT NULL, " +
                    "usu_t_sem_sem_id INTEGER NOT NULL, " +
                    "curso_id INTEGER NOT NULL, " +
                    "CONSTRAINT pk_composta PRIMARY KEY(curso_id, usu_t_sem_usu_id, " +
                    "usu_t_sem_sem_id) " +
                    /*"FOREIGN KEY(curso_id) REFERENCES curso(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_para_usu_em_um_sem FOREIGN KEY(usu_t_sem_usu_id, usu_t_sem_sem_id) " +
                    "REFERENCES usuario_tem_semestre(usu_id, sem_id)" +
                        "ON DELETE CASCADE," +*/
                    ");",
            "CREATE TABLE usuario_tem_semestre_tem_curso_tem_disciplina_tem_nota( " +
                    "usu_t_sem_t_curso_usu_t_sem_usu_id INTEGER NOT NULL," +
                    "usu_t_sem_t_curso_usu_t_sem_sem_id INTEGER NOT NULL," +
                    "usu_t_sem_t_curso_curso_id INTEGER NOT NULL," +
                    "disc_id INTEGER NOT NULL," +
                    "nota_id INTEGER," +
                    "CONSTRAINT pk_composta PRIMARY KEY(disc_id, usu_t_sem_t_curso_curso_id, " +
                    "usu_t_sem_t_curso_usu_t_sem_usu_id, usu_t_sem_t_curso_usu_t_sem_sem_id)" +
                    /*"CONSTRAINT fk_para_usu_em_um_sem_em_um_curso FOREIGN KEY(usu_t_sem_t_curso_curso_id, " +
                        "usu_t_sem_t_curso_usu_t_sem_usu_id, usu_t_sem_t_curso_usu_t_sem_sem_id) " +
                        "REFERENCES usuario_tem_semestre_tem_curso(curso_id, usu_t_sem_usu_id, usu_t_sem_sem_id) " +
                        "ON DELETE CASCADE," +
                    "FOREIGN KEY(disc_id) REFERENCES disciplina(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(nota_id) REFERENCES nota(id) ON DELETE CASCADE " +*/
                    ");"
    };



    private static final String NOME_DO_BANCO_DE_DADOS = "appgruda";

    private static final int VERSAO_DO_BANCO_DE_DADOS = 1;

    public CriadorDoBanco(Context ctx) {
        super(ctx, NOME_DO_BANCO_DE_DADOS, null, VERSAO_DO_BANCO_DE_DADOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(CATEGORIA_LOG, "Banco sendo criado.");
        String discPC = "";
        int idCurso = 1;
        try {
            for(String sql : SCRIPT_DE_CRIACAO_DO_BANCO_DE_DADOS) {
                db.execSQL(sql);
            }

            db.execSQL("INSERT INTO usuario(nome, linkfacebook) values('Anastácia', 'ancia')");

            //insere os cursos e as diciplinas
             //o id do curso
            for(CursoEDisciplinas cd : CURSOS_E_DISCIPLINAS) { //pega cada curso e suas disciplinas

                db.execSQL("INSERT INTO curso(descricao) VALUES('" + cd.getCurso() + "');");
                for(String discParaCurso : cd.getDisciplinas()) { //pega as disciplinas do curso

                    discPC = discParaCurso;
                    db.execSQL("INSERT INTO disciplina(descricao, curso_id) VALUES('" + discParaCurso + "', " + idCurso + ");");
                }
                idCurso += 1;
            }

            //insere os semestres
            //insere 25 anos apartir de 5 anos atrás
            long milisegundosCorrentes = System.currentTimeMillis();
            long anoCorrente = milisegundosCorrentes / (1000 * 60 * 60 * 24 * 365) + 1970;
            long fimWhile = anoCorrente + 25;
            long ano = anoCorrente - 5;
            while(ano <= fimWhile) {
                db.execSQL("INSERT INTO semestre(descricao) VALUES('" + ano + ".1');");
                db.execSQL("INSERT INTO semestre(descricao) VALUES('" + ano + ".2')");
                ano += 1;
            }

        } catch(SQLException ex) {
            Log.e(CATEGORIA_LOG, "Erro com a sintaxe do script de criação do banco: " + discPC + idCurso, ex);
            throw ex; //relança a excessão
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(CATEGORIA_LOG, "Banco sendo atualizado.");
        try {
            Log.i(CATEGORIA_LOG, "Banco desatualizado sendo deletado.");

            //deleta todas as tabelas do banco
            for(String sql : SCRIPT_DE_DELECAO_DO_BANCO_DE_DADOS) {
                db.execSQL(sql);
            }
            //cria novamente o banco
            onCreate(db);

        } catch(SQLException ex) {
            Log.e(CATEGORIA_LOG, "Erro com a sintaxe do script de deleção do banco.", ex);
            throw ex; //relança a excessão
        }
    }

    private static final CursoEDisciplinas[] CURSOS_E_DISCIPLINAS = new CursoEDisciplinas[]{
            new CursoEDisciplinas("ADMINISTRAÇÃO", new String[]{ //curso de admnistração
                    "CRIATIVIDADE E MOTIVAÇÃO",
                    "FUNDAMENTOS DA ADMINISTRAÇÃO",
                    "INFORMÁTICA",
                    "MATEMÁTICA BÁSICA",
                    "PORTUGUÊS INSTRUMENTAL",
                    "ESTATÍSTICA",
                    "EVOLUÇÃO DO PENSAMENTO ADMINISTRATIVO MODERNO",
                    "INTRODUÇÃO AO DIREITO",
                    "METODOLOGIA DO TRABALHO CIENTÍFICO",
                    "SOCIOLOGIA DAS ORGANIZAÇÕES",
                    "CONTABILIDADE GERENCIAL",
                    "ECONOMIA",
                    "GESTÃO MERCADOLÓGICA",
                    "MATEMÁTICA FINANCEIRA",
                    "PSICOLOGIA DO TRABALHO",
                    "DIREITO EMPRESARIAL",
                    "FILOSOFIA E ÉTICA PROFISSIONAL",
                    "GESTÃO DE RECURSOS MATERIAIS E PATRIMÔNIO",
                    "GESTÃO ESTRATÉGICA DE CUSTOS",
                    "PLANEJAMENTO E PESQUISA MERCADOLÓGICA",
                    "GESTÃO DE TALENTOS E PESSOAS",
                    "GESTÃO FINANCEIRA",
                    "ORGANIZAÇÃO, SISTEMAS E MÉTODOS",
                    "SIMULAÇÃO - LABORATÓRIO DE MARKETING",
                    "GESTÃO DA PRODUÇÃO",
                    "GESTÃO DA QUALIDADE",
                    "GESTÃO SOCIAL E AMBIENTAL",
                    "SIMULAÇÃO - GERENCIANDO EM DIREÇÃO À META",
                    "EMPREENDEDORISMO",
                    "ESTRATÉGIA EMPRESARIAL",
                    "ORÇAMENTO EMPRESARIAL",
                    "SIMULAÇÃO - TEORIA DAS RESTIRIÇÕES",
                    "CONSULTORIA E PROJETOS EMPRESARIAIS",
                    "SIMULAÇÃO - COMÉRCIO EXTERIOR",
                    "TÓPICOS ESPECIAIS EM ADMINISTRAÇÃO",
                    "TRABALHO DE CONCLUSÃO DE CURSO",
                    "ELETIVA I - COMPORTAMENTO DO CONSUMIDOR",
                    "ELETIVA I - GESTÃO DE MUDANÇAS",
                    "ELETIVA I - GESTÃO DE SISTEMAS DE INFORMAÇÃO",
                    "ELETIVA I - GESTÃO DE VENDAS",
                    "ELETIVA I - GESTÃO ESTRATÉGICA DE PESSOAS",
                    "ELETIVA I - LOGÍSTICA EMPRESARIAL",
                    "ELETIVA I - TÉCNICAS DE NEGOCIAÇÃO"
            }),
            new CursoEDisciplinas("ANÁLISE E DESENVOLVIMENTO DE SISTEMAS", new String[]{
                    "INTRODUÇÃO A ALGORITMOS E PROGRAMAÇÃO",
                    "ÉTICA, DIREITO, SOCIEDADE E RESPONSABILIDADE SOCIOAMBIENTAL",
                    "MATEMÁTICA APLICADA",
                    "INGLÊS INSTRUMENTAL",
                    "BANCO DE DADOS 1",
                    "ORGANIZAÇÃO SISTEMAS E MÉTODOS",
                    "ARQUITETURA E ORGANIZAÇÃO DE COMPUTADORES",
                    "BANCO DE DADOS 2",
                    "LINGUAGEM DE PROGRAMAÇÃO - WEB",
                    "ENGENHARIA DE SOFTWARE",
                    "PROGRAMAÇÃO ORIENTADA A OBJETO 1",
                    "REDE DE COMPUTADORES E SISTEMAS DISTRIBUIDOS",
                    "ANÁLISE E DESENVOLVIMENTO DE SOFTWARE",
                    "INTERAÇÃO HUMANO-COMPUTADOR",
                    "SISTEMAS DE INFORMAÇÕES GERENCIAIS",
                    "PROGRAMAÇÃO ORIENTADA A OBJETO 2",
                    "GESTÃO DE PROJETOS DE TI",
                    "PROJETO INTEGRADOR 1",
                    "SISTEMAS OPERACIONAIS",
                    "PROJETO E DESENVOLVIMENTO DE SOFTWARE",
                    "PROJETO INTEGRADOR 2",
                    "PROGRAMAÇÃO PARA DISPOSITIVOS MÓVEIS",
                    "EMPREENDEDORISMO E INOVAÇÃO",
                    "ESTRUTURA DE DADOS E ORGANIZAÇÃO DE ARQUIVOS",
                    "SEGURANÇA DA INFORMAÇÃO",
                    "TÓPICOS ESPECIAIS EM ANÁLISE E DESENVOLVIMENTO DE SISTEMAS",
                    "TESTE, VALIDAÇÃO E QUALIDADE DE SOFTWARE",
                    "PROJETO INTEGRADOR 3",
                    "ATIVIDADES COMPLEMENTARES",
                    "LÍNGUA BRASILEIRA DE SINAIS - LIBRAS(OPTATIVA)",
                    "GOVERNAÇA E GESTÃO DE CONFIGURAÇÃO(OPTATIVA)",
                    "MODELAGEM DE PROCESSO DE NEGÓCIO(OPTATIVA)",
                    "APLICAÇÕES WEB COM JAVA(OPTATIVA)",
                    "GAMIFICAÇÃO E DESENVOLVIMENTO DE JOGOS(OPTATIVA)",
                    "INTELIGÊNCIA ARTIFICIAL(OPTATIVA)",
                    "MINERAÇÃO DE DADOS(OPTATIVA)",
                    "ARQUITETURA E PADRÕES DE DESENVOLVIMENTO DE SOFTWARE(OPTATIVA)",
                    "PARADIGMAS DE PROGRAMAÇÃO(OPTATIVA)"
            }),
            new CursoEDisciplinas("BIOMEDICINA", new String[]{
                    "BIOSSEGURANÇA",
                    "CITOLOGIA, HISTOLOGIA E EMBRIOLOGIA",
                    "FUNDAMENTOS E HISTÓRICO DA BIOMEDICINA",
                    "MATEMÁTICA APLICADA AO LABORATÓRIO",
                    "QUÍMICA APLICADA I",
                    "SÓCIO-ANTROPOLOGIA DA SAÚDE",
                    "METODOLOGIA DO TRABALHO CIENTÍFICO",
                    "ANATOMIA HUMANA",
                    "BIOINFORMÁTICA",
                    "BIOQUÍMICA BÁSICA",
                    "FISIOLOGIA HUMANA",
                    "GENÉTICA E EVOLUÇÃO",
                    "LEITURA, PRODUÇÃO E INTERPRETAÇÃO TEXTUAL",
                    "QUÍMICA APLICADA II",
                    "SAÚDE AMBIENTAL",
                    "SAÚDE PÚBLICA",
                    "BIOFÍSICA",
                    "ÉTICA PROFISSIONAL E BIOÉTICA",
                    "BIOESTATÍSTICA",
                    "BIOLOGIA MOLECULAR",
                    "BIOQUÍMICA METABÓLICA",
                    "CITOGENÉTICA",
                    "HISTOLOGIA E EMBRIOLOGIA DOS SISTEMAS",
                    "MICROBIOLOGIA E IMUNOLOGIA",
                    "PARASITOLOGIA",
                    "ANÁLISE FÍSICO-QUÍMICA DA ÁGUA",
                    "EPIDEMIOLOGIA",
                    "FUNDAMENTOS DE FARMACOLOGIA",
                    "GENÉTICA MOLECULAR",
                    "PATOLOGIA GERAL",
                    "RADIOBIOLOGIA",
                    "URGÊNCIA EM SAÚDE",
                    "ANÁLISE MICROBIOLÓGICA DA ÁGUA",
                    "BIOQUÍMICA CLÍNICA",
                    "BROMATOLOGIA",
                    "FARMACOLOGIA APLICADA E TOXICOLOGIA",
                    "HEMATOLOGIA",
                    "PARASITOLOGIA CLÍNICA",
                    "PATOLOGIA DE ÓRGÃOS E SISTEMAS",
                    "BIOIMAGEM",
                    "BACTERIOLOGIA CLÍNICA",
                    "CITOPATOLOGIA",
                    "HEMATOLOGIA CLÍNICA E HEMOTERAPIA",
                    "IMUNOLOGIA CLÍNICA",
                    "UROANÁLISES E FLUÍDOS CORPORAIS",
                    "VIROLOGIA CLÍNICA",
                    "MICOLOGIA CLÍNICA",
                    "ELETIVA I.",
                    "ESTÁGIO CURRICULAR SUPERVISIONADO 1",
                    "TRABALHO DE CONCLUSÃO DE CURSO I",
                    "ESTÁGIO CURRICULAR SUPERVISIONADO 2",
                    "TRABALHO DE CONCLUSÃO DE CURSO II",
                    "ATIVIDADES COMPLEMENTARES",
                    "EDUCAÇÃO PARA SAÚDE",
                    "EPIDEMIOLOGIA CLÍNICA",
                    "LÍNGUA BRASILEIRA DE SINAIS (LIBRAS)",
                    "TÓPICOS AVANÇADOS EM BIOMEDICINA I"
            }),
            new CursoEDisciplinas("CIÊNCIAS CONTÁBEIS", new String[]{
                    "INSTITUIÇÕES DE DIREITO PÚBLICO E PRIVADO",
                    "INFORMÁTICA",
                    "MATEMÁTICA BÁSICA",
                    "PORTUGUÊS INSTRUMENTAL",
                    "CONTABILIDADE BÁSICA",
                    "MATEMATICA FINANCEIRA",
                    "CONTABILIDADE GERAL",
                    "LEGISLAÇÃO SOCIAL E TRABALHISTA",
                    "FILOSOFIA E SOCIOLOGIA DAS ORGANIZAÇÕES",
                    "ESTATÍSTICA",
                    "CONTABILIDADE COMERCIAL",
                    "GESTÃO FINANCEIRA",
                    "ECONOMIA",
                    "FUNDAMENTOS DA ADMINISTRAÇÃO",
                    "METODOLOGIA DO TRABALHO CIENTÍFICO",
                    "LEGISLAÇÃO TRIBUTÁRIA E COMERCIAL",
                    "CONTABILIDADE TRIBUTÁRIA",
                    "TEORIA GERAL DA CONTABILIDADE",
                    "ESTRUTURA DAS DEMONSTRAÇÕES CONTÁBEIS",
                    "ÉTICA E LEGISLAÇÃO PROFISSIONAL",
                    "CONTABILIDADE SOCIETÁRIA",
                    "ELETIVA I",
                    "ANÁLISE DAS DEMONSTRAÇÕES CONTÁBEIS",
                    "DIREITO COMERCIAL E SOCIETÁRIO",
                    "PSICOLOGIA ORGANIZACIONAL",
                    "AUDITORIA",
                    "GESTÃO DE CUSTOS INDUSTRIAIS",
                    "ELETIVA II",
                    "ORÇAMENTAÇÃO E FINANÇAS PÚBLICAS",
                    "PRÁTICA CONTÁBIL I",
                    "PERÍCIA, AVALIAÇÃO E ARBITRAGEM",
                    "GESTÃO CONTÁBIL DO SETOR PÚBLICO",
                    "ELETIVA III",
                    "CONTROLADORIA",
                    "PRÁTICA CONTÁBIL II",
                    "ELETIVA IV",
                    "SISTEMA DE INFORMAÇÃO GERENCIAL",
                    "ESTÁGIO SUPERVISIONADO",
                    "TRABALHO DE CONCLUSÃO DE CURSO",
                    "ATIVIDADES COMPLEMENTARES",
                    "TÓPICOS AVANÇADOS",
                    "GESTÃO E RESPONSABILIDADE SÓCIOAMBIENTAL (ELETIVA)",
                    "PROJETOS: ELABORAÇÃO E GERENCIAMENTO (ELETIVA)",
                    "TÓPICOS ESPECIAIS EM CONTABILIDADE (ELETIVA)",
                    "CONTABILIDADE APLICADA AS ENTIDADES DE INTERESSE SOCIAL (ELETIVA)",
                    "CONTABILIDADE NACIONAL",
                    "GOVERNANÇA CORPORATIVA (ELETIVA)",
                    "LIBRAS (ELETIVA)",
                    "ANTROPOLOGIA, RELAÇÕES ÉTNICAS E RACIAIS (ELETIVA)",
                    "CONTABILIDADE DE GANHOS (ELETIVA)",
                    "CONTABILIDADE GERENCIAL (ELETIVA)",
                    "GESTÃO DA QUALIDADE (ELETIVA)",
                    "CRIATIVIDADE E MOTIVAÇÃO (ELETIVA)",
                    "CONTABILIDADE BANCÁRIA(ELETIVA)",
                    "ORÇAMENTO EMPRESARIAL (ELETIVA)",
                    "CONTABILIDADE SOCIAL",
                    "ROTINAS DE PESSOAL (ELETIVA)"
            }),
            new CursoEDisciplinas("EDUCAÇÃO FÍSICA", new String[]{
                    "PENSAMENTO PEDAGÓGICO E DIDÁTICA DA EDUCAÇÃO",
                    "TÉCNICAS DE COMUNICAÇÃO EXPRESSÃO E ESTUDOS ACADÊMICOS",
                    "ESPORTE, LAZER E SOCIEDADE",
                    "PSICOLOGIA DA EDUCAÇÃO",
                    "ASPECTOS HISTÓRICOS E SOCIAIS DA EDUCAÇÃO FÍSICA",
                    "FORMAÇÃO RITMICA DO MOVIMENTO",
                    "ANATOMIA HUMANA",
                    "EDUCAÇÃO FÍSICA ESCOLAR I",
                    "FUNDAMENTOS DA EDUCAÇÃO FÍSICA",
                    "METODOLOGIA DO ATLETISMO ESCOLAR I",
                    "METODOLOGIA DO VOLEIBOL ESCOLAR I",
                    "FISIOLOGIA HUMANA",
                    "CRESCIMENTO E DESENVOLVIMENTO MOTOR HUMANO",
                    "INFORMÁTICA",
                    "CINESIOLOGIA E BIOMECÂNICA ",
                    "PRÁTICA CURRICULAR I",
                    "METODOLOGIA DA GINÁSTICA ESCOLAR",
                    "EDUCAÇÃO FÍSICA ESCOLAR II",
                    "BIOESTATÍSTICA",
                    "METODOLOGIA DO VOLEIBOL ESCOLAR II ",
                    "METODOLOGIA DO ATLETISMO ESCOLAR II",
                    "INTRODUÇÃO À APREDIZAGEM MOTORA",
                    "DIDÁTICA DA EDUCAÇÃO FÍSICA",
                    "METODOLOGIA DO TRABALHO CIENTÍFICO",
                    "FISIOLOGIA DO EXERCÍCIO ",
                    "ESTRUTURA E FUNCIONAMENTO DO ENSINO FUNDAMENTAL E MÉDIO",
                    "URGÊNCIA EM SAÚDE",
                    "ESTÁGIO CURRICULAR SUPERVISIONADO I ",
                    "METODOLOGIA DO BASQUETEBOL ESCOLAR I",
                    "METODOLOGIA DO HANDEBOL ESCOLAR I",
                    "CINEATROPOMETRIA",
                    "ELETIVA I",
                    "PRÁTICA CURRICULAR II",
                    "EDUCAÇÃO FÍSICA PARA PESSOAS COM NECESSIDADES ESPECIAIS",
                    "ESTÁGIO CURRICULAR SUPERVISIONADO II ",
                    "ÉTICA PROFISSIONAL",
                    "METODOLOGIA DO BASQUETEBOL ESCOLAR II ",
                    "METODOLOGIA DO HANDEBOL ESCOLAR I ",
                    "METODOLOGIA DA NATAÇÃO I",
                    "EDUCAÇÃO FÍSICA E NUTRIÇÃO HUMANA",
                    "PRÁTICA CURRICULAR III",
                    "ESTÁGIO CURRICULAR SUPERVISIONADO III ",
                    "METODOLOGIA DO FUTEBOL E DO FUTSAL ESCOLAR",
                    "METODOLOGIA DA NATAÇÃO II",
                    "TÓPICOS ESPECIAIS EM EDUCAÇÃO FÍSICA",
                    "PRÁTICA CURRICULAR IV",
                    "ESTÁGIO CURRICULAR SUPERVISIONADO IV ",
                    "LÍNGUA BRASILEIRA DE SINAIS (LIBRAS)",
                    "ELETIVA II",
                    "TRABALHO DE CONCLUSÃO DE CURSO I",
                    "ELETIVA III",
                    "DANÇA CONTEMPORÂNEA, FOLCLÓRICA E REGIONAL",
                    "POLÍTICAS PÚBLICAS, EDUCAÇÃO E EDUCAÇÃO FÍSICA",
                    "ATIVIDADES COMPLEMENTARES",
                    "TRABALHO DE CONCLUSÃO DE CURSO II",
                    "BIOQUÍMICA METABÓLICA",
                    "METODOLOGIA DA MUSCULAÇÃO (ELETIVA)",
                    "GINÁSTICA EM ACADEMIA (ELETIVA)",
                    "ATIVIDADE FÍSICA PARA 3ª IDADE (ELETIVA)",
                    "ATIVID. FÍSICAS RADICAIS E DE AVENTURA (ELETIVA)",
                    "METODOLOGIA DA HIDROGINÁSTICA (ELETIVA)",
                    "FUNDAMENTOS DOS TREINAMENTO DESPORTIVO (ELETIVA)",
                    "ADM. E GESTÃO EM ED. FÍSICA EM ESPORTE (ELETIVA)",
                    "METODOLOGIA DO ENSINO DA LUTAS (ELETIVA)",
                    "PSICOMOTRICIDADE." //analisar
            }),
            new CursoEDisciplinas("ENFERMAGEM", new String[]{
                    "ANATOMIA HUMANA",
                    "CITOLOGIA E HISTOLOGIA",
                    "HISTÓRIA DA ENFERMAGEM",
                    "BIOFÍSICA",
                    "EMBRIOLOGIA",
                    "INFORMÁTICA",
                    "BIOQUÍMICA BÁSICA",
                    "IMUNOLOGIA",
                    "URGÊNCIA EM SAÚDE",
                    "MICROBIOLOGIA",
                    "BIOESTATÍSTICA",
                    "PRODUÇÃO E INTERPRETAÇÃO TEXTUAL",
                    "EVOLUÇÃO E GENÉTICA",
                    "FISIOLOGIA",
                    "EXERCÍCIO DE ENFERMAGEM",
                    "SEMIOLOGIA E SEMIOTÉCNICA DE ENFERMAGEM I",
                    "SAÚDE AMBIENTAL",
                    "PARASITOLOGIA",
                    "FARMACOLOGIA I ",
                    "SÓCIO-ANTROPOLOGIA DA SAÚDE",
                    "METODOLOGIA DO TRABALHO CIENTÍFICO",
                    "SEMIOLOGIA E SEMIOTÉCNICA EM ENFERMAGEM II ",
                    "PATOLOGIA GERAL ",
                    "EPIDEMIOLOGIA NA SAÚDE",
                    "FARMACOLOGIA II",
                    "OPTATIVA I - ANÁLISE DE EXAMES LABORATORIAIS",
                    "PSICOLOGIA APLICADA À SAÚDE",
                    "ENFERMAGEM EM SAÚDE MENTAL",
                    "PROCESSO DE TRABALHO EM ENFERMAGEM",
                    "ENFERMAGEM EM SAÚDE COLETIVA ",
                    "GERENCIAMENTO DO TRABALHO EM ENFERMAGEM NA ATENÇÃO BÁSICA",
                    "OPTATIVA II - PRÁTICAS COMPLEMENTARES E INTEGRATIVAS",
                    "ENFERMAGEM CLÍNICA EM SAÚDE DO ADULTO ",
                    "GERENCIAMENTO DO TRABALHO EM ENFERMAGEM HOSPITALAR",
                    "ENFERMAGEM CIRURGICA EM SAÚDE DO ADULTO ",
                    "ENFERMAGEM ONCOLÓGICA",
                    "ENFERMAGEM EM SAÚDE DO IDOSO ",
                    "ENFERMAGEM EM SAÚDE DA MULHER E DO RECÉM-NASCIDO ",
                    "ENFERMAGEM EM SAÚDE DA CRIANÇA E DO ADOLESCENTE",
                    "ENFERMAGEM EM UNIDADE DE TERAPIA INTENSIVA ",
                    "PROCESSO ENSINO-APREDIZAGEM",
                    "ENFERMAGEM EM EMERGÊNCIA ",
                    "ENFERMAGEM EM SAÚDE DO TRABALHADOR",
                    "OPTATIVA III - NUTRIÇÃO E DIETÉTICA",
                    "TÓPICOS ESPECIAIS EM ENFERMAGEM",
                    "TRABALHO DE CONCLUSÃO DE CURSO I ",
                    "ESTÁGIO SUPERVISIONADO NA ATENÇÃO BÁSIC ",
                    "ENFERMAGEM EM SAÚDE DO HOMEM, GÊNERO E SEXUALIDADE",
                    "TRABALHO DE CONCLUSÃO DE CURSO II ",
                    "ESTÁGIO SUPERVISIONADO NA REDE HOSPITALAR ",
                    "ATIVIDADES COMPLEMENTARES"
            }),
            new CursoEDisciplinas("FISIOTERAPIA", new String[]{
                    "ANATOMIA HUMANA",
                    "BIOQUÍMICA",
                    "CITOLOGIA, HISTOLOGIA E EMBRIOLOGIA",
                    "INFORMÁTICA",
                    "ANTROPOLOGIA, SOCIEDADE E MEIO AMBIENTE",
                    "HISTÓRIA E FUNDAMENTOS DE FISIOTERAPIA",
                    "ANATOMIA FUNCIONAL ",
                    "BIOFÍSICA",
                    "FISIOLOGIA HUMANA",
                    "METODOLOGIA DA PESQUISA E DO TRABALHO CIENTÍFICO",
                    "PSICOLOGIA APLICADA À SAÚDE",
                    "PORTUGUÊS E INTERPRETAÇÃO DE TEXTO",
                    "URGÊNCIA EM SAÚDE",
                    "AVALIAÇÃO CLÍNICA EM FISIOTERAPIA",
                    "CINESIOLOGIA E BIOMECÂNICA ",
                    "ÉTICA E DIREITOS HUMANOS EM FISITERAPIA",
                    "FISIOLOGIA DO EXERCÍCIO ",
                    "IMUNOLOGIA",
                    "PATOLOGIA GERAL",
                    "BIOESTATÍSTICA",
                    "CINESIOTERAPIA ",
                    "FUNDAMENTOS DA FARMACOLOGIA",
                    "RECURSOS TERAPÊUTICOS MANUAIS E MECÂNICOS ",
                    "SAÚDE COLETIVA E EPIDEMIOLOGIA",
                    "ELETROTERAPIA, FOTOTERAPIA E TERMOTERAPIA",
                    "EXAMES COMPLEMENTARES",
                    "FISIOTERAPIA NEUROFUNCIONAL CLÍNICA ",
                    "FISIOTERAPIA PENUMOFUNCIONAL CLÍNICA ",
                    "FISIOTERAPIA REUMATOLÓGICA",
                    "FISIOTERAPIA TRAUMATO-ORTOPÉDICA CLÍNICA",
                    "HIDROTERAPIA ",
                    "SAÚDE COLETIVA APLICADA À FISIOTERAPIA",
                    "FISIOTERAPIA DESPORTIVA ",
                    "FISIOTERAPIA EM ÓRTESE E PRÓTESE",
                    "FISIOTERAPIA PNEUMOFUNCIONAL APLICADA ",
                    "FISIOTERAPIA TRAUMATO-ORTOPÉDICA APLICADA ",
                    "FISIOTERAPIA EM PEDIATRIA E NEONATOLOGIA CLÍNICA",
                    "PSICOMOTRICIDADE",
                    "FISIOTERAPIA NEUROFUNCIONAL APLICADA",
                    "FISIOTERAPIA CARDIOFUNCIONAL CLÍNICA",
                    "FISIOTERAPIA DERMATOFUNCIONAL ",
                    "FISIOTERAPIA INTENSIVA",
                    "SAÚDE DO TRABALHO E AMBIENTAL ",
                    "FISIOTERAPIA URO-GINECO-OBSTETRÍCA ",
                    "OPTATIVA I",
                    "FISIOTERAPIA APLICADA À PEDIATRIA ",
                    "FISIOTERAPIA CARDIOFUNCIONAL APLICADA ",
                    "FISIOTERAPIA EM GERIATRIA E GERONTOLOGIA",
                    "FISIOTERAPIA VASCULAR",
                    "GESTÃO E PLANEJAMENTO EM FISIOTERAPIA",
                    "OPTATIVA II",
                    "FISIOTERAPIA APLICADA AS TERAPIAS COMPLEMENTARES",
                    "ESTÁGIO SUPERVISIONADO I ",
                    "TÓPICOS ESPECIAIS EM FISIOTERAPIA I",
                    "TRABALHO DE CONCLUSAO DE CURSO I ",
                    "ESTÁGIO SUPERVISIONADO II ",
                    "TÓPICOS ESPECIAIS EM FISIOTERAPIA II ",
                    "TRABALHO DE CONCLUSAO DE CURSO II ",
                    "ATIVIDADES COMPLEMENTARES"
            }),
            new CursoEDisciplinas("GESTÃO COMERCIAL", new String[]{
                    "FUNDAMENTOS DA GESTÃO",
                    "MATEMÁTICA E ESTATÍSTICA",
                    "ECONOMIA E MERCADO",
                    "PLANEJAMENTO ESTRATÉGICO DE MARKETING",
                    "GESTÃO DE PESSOAS",
                    "DIREITO APLICADO À GESTÃO",
                    "COMPORTAMENTO DO CONSUMIDOR",
                    "MATEMÁTICA FINANCEIRA",
                    "ATENDIMENTO E RELACIONAMENTO COM O CLIENTE",
                    "PROJETO INTEGRADOR I - ÊNFASE EM COMUNICAÇÃO",
                    "GESTÃO DE COMPRAS E ESTOQUES",
                    "LOGÍSTICA E CANAIS DE DISTRIBUIÇÃO",
                    "SISTEMAS DE INFORMAÇÕES COMERCIAIS",
                    "CONTABILIDADES",
                    "PROJETO INTEGRADOR II - ÊNFASE EM ESTRATÉGIA",
                    "GESTÃO DA QUALIDADE",
                    "COMÉRCIO ELETRÔNICO E NEGOCIOS NA INTERNET",
                    "CUSTOS E FORMAÇÃO DE PREÇOS",
                    "TÉCNICAS DE VENDAS E NEGOCIAÇÃO",
                    "PROJETO INTEGRADOR III",
                    "REDE DE AUTO-SERVIÇOS, SHOPPING CENTERS E FRANQUIAS",
                    "ÉTICA PROFISSIONAL E RESPONSABILIDADE SOCIAL",
                    "EMPREENDEDORISMO",
                    "PROJETO INTEGRADOR IV",
                    "ATIVIDADES COMPLEMENTARES",
                    "LIBRAS - LÍNGUA BRASILEIRA DE SINAIS",
                    "TÓPICOS ESPECIAIS EM GESTÃO COMERCIAL"
            }),
            new CursoEDisciplinas("GESTÃO DE RECURSOS HUMANOS", new String[]{
                    "FUNDAMENTOS DA GESTÃO",
                    "TEORIA ECONÔMICA",
                    "CONTABILIDADE BÁSICA",
                    "DIREITO APLICADO À GESTÃO",
                    "COMUNICAÇÃO EMPRESARIAL",
                    "GESTÃO DE RECURSOS HUMANOS",
                    "SISTEMAS DE INFORMAÇÃO DE RECURSOS HUMANOS",
                    "ROTINAS DE PESSOAL",
                    "SEGURANÇA DO TRABALHO E SAÚDE OCUPACIONAL",
                    "RELAÇÕES INTERPESSOAIS NAS ORGANIZAÇÕES E ÉTICA",
                    "RECRUTAMENTO E SELEÇÃO DE PESSOAL",
                    "TREINAMENTO, DESENVOLVIMENTO E FORMAÇÃO DE LIDERANÇA",
                    "GESTÃO DE CARREIRAS E AVALIAÇÃO DE DESEMPENHO",
                    "GESTÃO DE CARGOS, SALÁRIOS E SISTEMAS DE BENEFÍCIOS",
                    "PROJETO INTEGRADOR I",
                    "NEGOCIAÇÃO E MEDIAÇÃO DE CONFLITOS",
                    "EMPREENDEDORISMO",
                    "PLANEJAMENTO ESTRATÉGICO EM RECURSOS HUMANOS",
                    "PROJETO INTEGRADOR II",
                    "CONSULTORIA EM GESTÃO DE RECURSOS HUMANOS",
                    "AVALIAÇÃO DO CLIMA ORGANIZACIONAL",
                    "PROGRAMAS DE QUALIDADE DE VIDA DO TRABALHO",
                    "TÓPICOS ESPECIAIS EM GESTÃO DE RECURSOS HUMANOS",
                    "PROJETO INTEGRADOR III",
                    "ATIVIDADES COMPLEMENTARES"
            }),
            new CursoEDisciplinas("ODONTOLOGIA", new String[]{
                    "ANATOMIA E ESCULTURA DENTAL",
                    "ANATOMIA HUMANA",
                    "BIOFÍSICA",
                    "BIOQUÍMICA BÁSICA",
                    "GENÉTICA E EVOLUÇÃO",
                    "INFORMÁTICA",
                    "CITOLOGIA, HISTOLOGIA E EMBRIOLOGIA",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 1",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 2",
                    "BIOESTATÍSTICA",
                    "MICROBIOLOGIA E IMUNOLOGIA",
                    "PATOLOGIA GERAL",
                    "ANATOMIA BUCO-FACIAL",
                    "FISIOLOGIA HUMANA",
                    "HISTOLOGIA E EMBRIOLOGIA BUCAL",
                    "CARIOLOGIA",
                    "METODOLOGIA DA PESQUISA E DO TRABALHO CIENTÍFICO",
                    "IMAGENOLOGIA",
                    "PATOLOGIA BUCAL",
                    "FARMACOLOGIA",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 3 ",
                    "INTRODUÇÃO AOS MATERIAIS DENTÁRIOS",
                    "PERIODONTIA 1",
                    "BIOSSEGURANÇA E ERGONOMIA EM ODONTOLOGIA",
                    "DENTÍSTICA 1 ",
                    "ANESTESIOLOGIA ODONTOLÓGICA ",
                    "CIRURGIA ODONTOLÓGICA 1 ",
                    "SEMIOLOGIA E ESTOMATOLOGIA ",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 4 ",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INTEGRADA I ",
                    "FARMACOLOGIA APLICADA A ODONTOLOGIA",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 5 ",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INTEGRADA II ",
                    "PSICOLOGIA APLICADA À ODONTOLOGIA ",
                    "ENDODONTIA 1 ",
                    "PRÓTESE 1",
                    "PRÓTESE 2 ",
                    "ENDODONTIA 2 ",
                    "PERIODONTIA 2 ",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INTEGRADA III ",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 6",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INTEGRADA IV ",
                    "DENTÍSTICA 2 ",
                    "CIRURGIA ODONTOLÓGICA 2 ",
                    "ODONTOLOGIA LEGAL ",
                    "ODONTOLOGIA, SOCIEDADE E MEIO AMBIENTE ",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 7 ",
                    "PRÓTESE 3",
                    "IMPLANTODONTIA ",
                    "TRABALHO DE CONCLUSÃO DE CURSO I ",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INTEGRADA V ",
                    "ODONTOLOGIA INFANTIL ",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 8",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INTEGRADA VI ",
                    "OPTATIVA 1 ",
                    "OPTATIVA 2 ",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 9 ",
                    "TRABALHO DE CONCLUSÃO DE CURSO II ",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INFANTIL I ",
                    "ADMINISTRAÇÃO DO CONSULTORIO ODONTOLÓGICO",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INFANTIL II ",
                    "TÓPICOS ESPECIAIS EM ODONTOLOGIA ",
                    "OPTATIVA 3 ",
                    "OPTATIVA 4 ",
                    "POLÍTICAS PÚBLICAS DE SAÚDE 10 ",
                    "ESTÁGIO SUPERVISIONADO EM CLÍNICA INTEGRADA VII",
                    "URGÊNCIA E EMERGÊNCIA EM ODONTOLOGIA (OPTATIVA) ",
                    "FOTOGRAFIA EM ODONTOLOGIA (OPTATIVA) ",
                    "TRAUMATISMO DENTÁRIO NA INFÂNCIA (OPTATIVA) ",
                    "ODONTOLOGIA PARA BEBÊS E GESTANTES (OPTATIVA) ",
                    "ODONTOLOGIA PARA PACIENTES COM FISSURAS LABIOPALATINAS (OPTATIVA) ",
                    "PROTOCOLOS DE TERAPIA MEDICAMENTOSA EM ODONTOLOGIA (OPTATIVA) ",
                    "ATUALIDADES EM ODONTOLOGIA 1 (OPTATIVA) ",
                    "ATUALIDADES EM ODONTOLOGIA 2 (OPTATIVA) "
            }),
            new CursoEDisciplinas("SERVIÇO SOCIAL", new String[]{
                    "INTRODUÇÃO AO SERVIÇO SOCIAL",
                    "FILOSOFIA",
                    "FUNDAMENTOS SOCIOLÓGICOS",
                    "ECONOMIA POLÍTICA",
                    "LEITURA E INTERPRETAÇAO DE TEXTO",
                    "METODOLOGIA DO TRABALHO CIENTÍFICO",
                    "ANTROPOLOGIA SOCIAL",
                    "DIREITO E LEGISLAÇÃO SOCIAL",
                    "FUNDAMENTOS HIST. E TEÓRICO-METODOLÓG. DO SERVIÇO SOCIAL I ",
                    "FORMAÇÃO SÓCIO-ECONÔMICA DO BRASIL",
                    "FUNDAMENTOS HIST. E TEÓRICO-METODOLÓG. DO SERVIÇO SOCIAL II ",
                    "CIÊNCIA POLÍTICA",
                    "POLÍTICA SOCIAL I",
                    "QUESTÃO SOCIAL",
                    "PSICOLOGIA SOCIAL",
                    "INFORMÁTICA",
                    "FUNDAMENTOS HIST. E TEÓRICO-METODOLÓG. DO SERVIÇO SOCIAL III ",
                    "POLÍTICA SOCIAL II ",
                    "CLASSES E MOVIMENTOS SOCIAIS I",
                    "TRABALHO E SOCIABILIDADE",
                    "MÉTODOS QUANTITATIVOS",
                    "FUNDAMENTOS HIST. E TEÓRICO-METODOLÓG. DO SERVIÇO SOCIAL IV ",
                    "ÉTICA PROFISSIONAL",
                    "CLASSES E MOVIMENTOS SOCIAIS II ",
                    "PROCESSOS DE TRABALHO E O SERVIÇO SOCIAL",
                    "PESQUISA SOCIAL",
                    "ADMINISTRAÇÃO EM SERVIÇO SOCIAL",
                    "SERVIÇO SOCIAL E SEGURIDADE SOCIAL",
                    "METODOLOGIA DO TRABALHO SOCIAL",
                    "ESTÁGIO SUPERVISIONADO I",
                    "INSTRUMENTALIDADE DO SERVIÇO SOCIAL",
                    "PLANEJAMENTO EM SERVIÇO SOCIAL",
                    "OPTATIVA I",
                    "OPTATIVA II",
                    "ESTÁGIO SUPERVISIONADO II",
                    "ORIENTAÇÃO DE TRABALHO DE CONCLUSÃO DE CURSO (TCC) ",
                    "GESTÃO SOCIAL",
                    "OPTATIVA III",
                    "SERVIÇO SOCIAL E POLITICA DE ATENÇÃO A CRIANÇA E ADOLESCENTE",
                    "SERVIÇO SOCIAL E POLITICA DE ATENÇÃO À FAMILIA",
                    "GERONTOLOGIA SOCIAL",
                    "SERVIÇO SOCIAL E O TERCEIRO SETOR",
                    "SERVIÇO SOCIAL E O TRABALHO JURÍDICO",
                    "DIREITOS HUMANOS",
                    "TÓPICOS ESPECIAIS EM SERVIÇO SOCIAL",
                    "LÍNGUA BRASILEIRA DE SINAIS (LIBRAS)",
                    "TÓPICOS ESPECIAIS EM SAÚDE (OPTATIVA)"
            })
    }; //CURSOS_E_DISCIPLINAS

    private static final class CursoEDisciplinas {
        private final String curso;
        private final String[] disciplinas;

        public CursoEDisciplinas(String curso, String[] disciplinas) {
            this.curso = curso;
            this.disciplinas = disciplinas;
        }

        public String getCurso() {
            return curso;
        }

        public String[] getDisciplinas() {
            return disciplinas;
        }
    }

}
