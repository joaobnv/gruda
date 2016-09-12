package br.bancogruda2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.bancogruda2.DAO.RepositorioCurso;
import br.bancogruda2.entidades.Curso;

public class MainActivity extends AppCompatActivity {

    private EditText descricao;
    private Button butaoCad;
    private RepositorioCurso repCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repCurso = new RepositorioCurso(this);

        descricao = (EditText) findViewById(R.id.edtDescCurso);
        butaoCad = (Button) findViewById(R.id.btCadCurso);

        butaoCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descricao.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this,"Digite a descrição.", Toast.LENGTH_LONG).show();
                    descricao.requestFocus();
                    return;
                }
                Curso curso = new Curso();
                curso.setDescricao(descricao.getText().toString());
                repCurso.inserir(curso);
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        repCurso.close();
    }

}
