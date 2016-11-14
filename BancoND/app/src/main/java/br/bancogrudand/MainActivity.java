package br.bancogrudand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import br.bancogrudand.DAO.RepositorioUsuario;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.teste2.usuario.ListarSemestrePorUsuario;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btCancelar;
    private Button btAvancar;

    private List<Usuario> listaDeUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btCancelar = (Button) findViewById(R.id.bt_cancelar);
        btAvancar  = (Button) findViewById(R.id.bt_avancar);

        RepositorioUsuario repUsu = new RepositorioUsuario(this);
        listaDeUsuarios = repUsu.listar();
        repUsu.close();

        List<String> nomesList         = new ArrayList<String>(listaDeUsuarios.size());
        List<String> linkFacebooksList = new ArrayList<String>(listaDeUsuarios.size());
        for(Usuario usu : listaDeUsuarios) {
            nomesList.add(usu.getNome());
            linkFacebooksList.add(usu.getLinkFacebook());
        }

        btCancelar.setOnClickListener(this);
        btAvancar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == btCancelar) {
            finish();
        } else if(v == btAvancar) {
            Intent it = new Intent(this, ListarSemestrePorUsuario.class);
            Bundle extras = new Bundle();
            long idUsu = 1;
            extras.putLong("idUsu", idUsu);
            it.putExtras(extras);
            startActivity(it);
        }
    }
}
