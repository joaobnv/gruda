package br.bancogrudand.teste2.usuario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import br.bancogrudand.DAO.RepositorioUsuarioTemSemestreTemCurso;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Curso;
import br.bancogrudand.entidades.Semestre;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.entidades.UsuarioTemSemestre;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCurso;
import br.bancogrudand.teste2.listar.CursoListAdapter;
import br.bancogrudand.teste2.listar.CursoPorSemestrePorUsuarioListAdapter;
import br.bancogrudand.teste2.listar.SemestreListAdapter;

/**
 * Created by JOÃO BRENO on 25/09/2016.
 */
public class addCursoParaSemestrePorUsuario extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private Button btCancelar;
    private Button btAdicionar;
    private ListView listView;
    private long idUsu, idSem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        Intent it = getIntent();
        Bundle extras = it.getExtras();
        idUsu = extras.getLong("idUsu");
        idSem = extras.getLong("idSem");

        btCancelar  = (Button)   findViewById(R.id.bt_cancelar);
        btAdicionar = (Button)   findViewById(R.id.bt_adicionar);
        listView    = (ListView) findViewById(R.id.lv_listar);

        btAdicionar.setVisibility(View.GONE);

        btCancelar.setOnClickListener(this);

        listView.setAdapter(new CursoListAdapter(this));

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
    public void onItemClick(AdapterView<?> parent, View view, int position, final long idClickedCurso) {
        new AlertDialog.Builder(this)
                .setTitle("Escolha")
                .setMessage("Adicionar")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nada
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UsuarioTemSemestre usuTemSem = new UsuarioTemSemestre(new Usuario(idUsu, null, null), new Semestre(idSem, null));
                        Curso curso = new Curso(idClickedCurso, null);

                        UsuarioTemSemestreTemCurso usc = new UsuarioTemSemestreTemCurso();
                        usc.setUsuarioTemSemestre(usuTemSem);
                        usc.setCurso(curso);

                        RepositorioUsuarioTemSemestreTemCurso repUSC =
                                new RepositorioUsuarioTemSemestreTemCurso(addCursoParaSemestrePorUsuario.this);
                        long retIns = repUSC.inserir(usc);
                        repUSC.close();

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

                        Toast toast = new Toast(addCursoParaSemestrePorUsuario.this);
                        toast.setView(layoutToast);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();

                        finish();
                    }
                }).show();
    }
}