package br.bancogrudand.teste2.usuario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import br.bancogrudand.DAO.RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Curso;
import br.bancogrudand.entidades.Disciplina;
import br.bancogrudand.entidades.Semestre;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.entidades.UsuarioTemSemestre;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCurso;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCursoTemDisciplinaTemNota;
import br.bancogrudand.teste2.listar.DisciplinaPorCursoListAdapter;

/**
 * Created by JOÃO BRENO on 25/09/2016.
 */
public class addDisciplinaParaCursoPorSemestrePorUsuario extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener{

    private Button btCancelar;
    private Button btAdicionar;
    private ListView listView;
    private long idUsu, idSem, idCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        Intent it = getIntent();
        Bundle extras = it.getExtras();
        idUsu = extras.getLong("idUsu");
        idSem = extras.getLong("idSem");
        idCurso = extras.getLong("idCurso");

        Log.i("addDiscCurSemUsu", "idUsu = " + idUsu + " idSem = " + idSem + " idCurso = " + idCurso);

        btCancelar  = (Button)   findViewById(R.id.bt_cancelar);
        btAdicionar = (Button)   findViewById(R.id.bt_adicionar);
        listView    = (ListView) findViewById(R.id.lv_listar);

        btAdicionar.setVisibility(View.GONE);

        btCancelar.setOnClickListener(this);

        listView.setAdapter(new DisciplinaPorCursoListAdapter(this, idCurso));

        listView.setOnItemClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if(v == btCancelar) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, final long idClickedDisc) {
        new AlertDialog.Builder(this)
                .setTitle("Escolha")
                .setMessage("Adicionar?")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nada
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Disciplina disc = new Disciplina(idClickedDisc, null, new Curso(idCurso, null));
                        Curso curso = disc.getCurso();

                        UsuarioTemSemestreTemCurso usc = new UsuarioTemSemestreTemCurso();
                        usc.setUsuarioTemSemestre(new UsuarioTemSemestre(new Usuario(idUsu, null, null), new Semestre(idSem, null)));
                        usc.setCurso(curso);

                        UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn = new UsuarioTemSemestreTemCursoTemDisciplinaTemNota();
                        uscdn.setDisciplina(disc);
                        uscdn.setUsuTemSemTemCurso(usc);

                        RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN =
                                new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(
                                        addDisciplinaParaCursoPorSemestrePorUsuario.this);
                        long retIns = repUSCDN.inserir(uscdn);
                        repUSCDN.close();

                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layoutToast = inflater.inflate(R.layout.toast, null);

                        ImageView img = (ImageView) layoutToast.findViewById(R.id.img_toast);

                        if(retIns >= 0) {
                            setResult(RESULT_OK);
                            img.setImageResource(R.drawable.certo_gruda1);
                        } else {
                            setResult(RESULT_CANCELED);
                            img.setImageResource(R.drawable.erro_gruda1);
                        }

                        Toast toast = new Toast(addDisciplinaParaCursoPorSemestrePorUsuario.this);
                        toast.setView(layoutToast);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();

                        finish();
                    }
                }).show();
    } //onItemClick
}
