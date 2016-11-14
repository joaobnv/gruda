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
import br.bancogrudand.teste2.listar.CursoPorSemestrePorUsuarioListAdapter;

/**
 * Created by JOÃO BRENO on 24/09/2016.
 */
public class ListarCursoPorUsuario extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int ADD_CURSO = 1;

    private Button btCancelar;
    private Button btAdicionar;
    private ListView listView;
    private long idUsu;
    private long idSem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        btCancelar  = (Button)   findViewById(R.id.bt_cancelar);
        btAdicionar = (Button)   findViewById(R.id.bt_adicionar);
        listView    = (ListView) findViewById(R.id.lv_listar);

        Intent it      = getIntent();
        Bundle extras  = it.getExtras();
        idUsu = extras.getLong("idUsu");
        idSem = extras.getLong("idSem");

        btCancelar.setOnClickListener(this);
        btAdicionar.setOnClickListener(this);

        atualizarLista();

        listView.setOnItemClickListener(this);

    }

    private void atualizarLista() {
        listView.setAdapter(new CursoPorSemestrePorUsuarioListAdapter(this, idUsu, idSem));
    }

    @Override
    public void onClick(View v) {
        if(v == btCancelar) {
            finish();
        } else if(v == btAdicionar) {
            Intent it = new Intent(this, addCursoParaSemestrePorUsuario.class);
            Bundle extras = new Bundle();
            extras.putLong("idUsu", idUsu);
            extras.putLong("idSem", idSem);
            it.putExtras(extras);
            startActivityForResult(it, ADD_CURSO);

            /*LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutToast = inflater.inflate(R.layout.toast, null);

            ImageView img = (ImageView) layoutToast.findViewById(R.id.img_toast);
            img.setImageResource(R.drawable.erro_gruda1);

            Toast toast = new Toast(this);
            toast.setView(layoutToast);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();

            Toast.makeText(this, "Not supported yet.", Toast.LENGTH_LONG).show();*/
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, final long idClickedCurso) {
        new AlertDialog.Builder(this)
                .setTitle("Escolha")
                .setMessage("Ver disciplinas?")
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nada
                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(ListarCursoPorUsuario.this, ListarDisciplinaPorUsuario.class);
                        Bundle extras = new Bundle();
                        extras.putLong("idUsu", idUsu);
                        extras.putLong("idSem", idSem);
                        extras.putLong("idCurso", idClickedCurso);
                        it.putExtras(extras);
                        startActivity(it);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case ADD_CURSO:
                if(resultCode == RESULT_OK) {
                    atualizarLista();
                }
                break;
            default:
                //nada
        }
    }

}
