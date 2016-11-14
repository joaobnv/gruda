package br.bancogrudand.teste2.usuario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import br.bancogrudand.R;
import br.bancogrudand.teste2.listar.SemestrePorUsuarioListAdapter;

/**
 * Created by JOÃO BRENO on 22/09/2016.
 */
public class ListarSemestrePorUsuario extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button btCancelar;
    private Button btAdicionar;
    private ListView listView;
    private long idUsu;

    private final int ADD_SEMESTRE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        btCancelar  = (Button)   findViewById(R.id.bt_cancelar);
        btAdicionar = (Button)   findViewById(R.id.bt_adicionar);
        listView    = (ListView) findViewById(R.id.lv_listar);

        Intent it      = getIntent();
        Bundle extras  = it.getExtras();
        idUsu          = extras.getLong("idUsu");

        btCancelar.setOnClickListener(this);
        btAdicionar.setOnClickListener(this);

        atualizarLista();

        listView.setOnItemClickListener(this);

    }

    private void atualizarLista() {
        listView.setAdapter(new SemestrePorUsuarioListAdapter(this, idUsu));
    }

    @Override
    public void onClick(View v) {
        if(v == btCancelar) {
            finish();
        } else if(v == btAdicionar) {
            Intent it = new Intent(this, addSemestreParaUsuario.class);
            Bundle extras = new Bundle();
            extras.putLong("idUsu", idUsu);
            it.putExtras(extras);
            startActivityForResult(it, ADD_SEMESTRE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, final long idClickedSem) {
        new AlertDialog.Builder(this)
                .setTitle("Escolha")
                .setMessage("Ver cursos?")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nada
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(ListarSemestrePorUsuario.this, ListarCursoPorUsuario.class);
                        Bundle extras = new Bundle();
                        extras.putLong("idUsu", idUsu);
                        extras.putLong("idSem", idClickedSem);
                        it.putExtras(extras);
                        startActivity(it);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case ADD_SEMESTRE:
                if(resultCode == RESULT_OK) {
                    atualizarLista();
                }
                break;
            default:
                //nada
        }
    }

}
