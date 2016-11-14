package br.bancogrudand.teste2.usuario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import br.bancogrudand.DAO.RepositorioNota;
import br.bancogrudand.DAO.RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Nota;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCursoTemDisciplinaTemNota;

/**
 * Created by LAB01 on 26/09/2016.
 */
public class novoEditarNota extends Activity implements View.OnClickListener{
    private long idUsu;
    private long idSem;
    private long idDisc;
    private long idCurso;
    private Nota nota;

    private EditText edtAv1;
    private EditText edtAv2;
    private EditText edtMedia;
    private EditText edtAvf;
    private EditText edtMediaFinal;
    private Button btCancelar,
            btOk;
    private UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_editar_nota);
        Intent it = getIntent();
        Bundle extras = it.getExtras();
        idUsu = extras.getLong("idUsu");
        idSem = extras.getLong("idSem");
        idCurso = extras.getLong("idCurso");
        idDisc = extras.getLong("idDisc");

        RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN =
                new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(this);
        uscdn = repUSCDN.buscar(idUsu, idSem, idCurso, idDisc);
        repUSCDN.close();

        nota = uscdn.getNota();

        edtAv1 = (EditText) findViewById(R.id.edt_av1);
        edtAv2 = (EditText) findViewById(R.id.edt_av2);
        edtMedia = (EditText) findViewById(R.id.edt_media);
        edtAvf = (EditText) findViewById(R.id.edt_avf);
        edtMediaFinal = (EditText) findViewById(R.id.edt_media_final);

        btCancelar = (Button) findViewById(R.id.bt_cancelar);
        btOk = (Button) findViewById(R.id.bt_ok);

        btCancelar.setOnClickListener(this);
        btOk.setOnClickListener(this);

        if(nota != null) {
            Double av1, av2, media, avf, mediaFinal;
            av1        = nota.getAv1();
            av2        = nota.getAv2();
            media      = nota.getMedia();
            avf        = nota.getAvf();
            mediaFinal = nota.getMediaFinal();

            if(av1 != null) {
                edtAv1.setText(String.valueOf(av1));
            }
            if(av2 != null) {
                edtAv2.setText(String.valueOf(av2));
            }
            if(media != null) {
                edtMedia.setText(String.valueOf(media));
            }
            if(avf != null) {
                edtAvf.setText(String.valueOf(avf));
            }
            if(mediaFinal != null) {
                edtMediaFinal.setText(String.valueOf(mediaFinal));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btCancelar) {
            setResult(RESULT_CANCELED);
            finish();
        } else if(v == btOk) {
            Double av1, av2, media, avf, mediaFinal;
            try {
                String strAv1, strAv2, strMedia, strAvf, strMediaFinal;
                strAv1 = edtAv1.getText().toString();
                strAv2 = edtAv2.getText().toString();
                strMedia = edtMedia.getText().toString();
                strAvf = edtAvf.getText().toString();
                strMediaFinal = edtMediaFinal.getText().toString();

                if(strAv1 != null && !strAv1.equals("")) {
                    av1 = Double.parseDouble(strAv1);
                } else {
                    av1 = null;
                }
                if(strAv2 != null && !strAv2.equals("")) {
                    av2 = Double.parseDouble(strAv2);
                } else {
                    av2 = null;
                }
                if(strMedia != null && !strMedia.equals("")) {
                    media = Double.parseDouble(strMedia);
                } else {
                    media = null;
                }
                if(strAvf != null && !strAvf.equals("")) {
                    avf = Double.parseDouble(strAvf);
                } else {
                    avf = null;
                }
                if(strMediaFinal != null && !strMediaFinal.equals("")) {
                    mediaFinal = Double.parseDouble(strMediaFinal);
                } else {
                    mediaFinal = null;
                }

            } catch(NumberFormatException ex) {
                Toast.makeText(this, "Erro. Valor mau formado.", Toast.LENGTH_LONG).show();
                return;
            }

            RepositorioNota repNota = new RepositorioNota(this);

            long res;

            if(nota == null) { //pra criar
                nota = new Nota();
                nota.setAv1(av1);
                nota.setAv2(av2);
                nota.setMedia(media);
                nota.setAvf(avf);
                nota.setMediaFinal(mediaFinal);

                res = repNota.inserir(nota);

                nota.setId(res);
                uscdn.setNota(nota);
                RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN =
                        new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(this);
                repUSCDN.atualizarNota(uscdn);
                repUSCDN.close();

            } else {//pra editar
                nota.setAv1(av1);
                nota.setAv2(av2);
                nota.setMedia(media);
                nota.setAvf(avf);
                nota.setMediaFinal(mediaFinal);

                res = repNota.atualizar(nota);
            }

            repNota.close();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutToast = inflater.inflate(R.layout.toast, null);

            ImageView img = (ImageView) layoutToast.findViewById(R.id.img_toast);

            if(res >= 0) { //sucesso
                setResult(RESULT_OK);
                img.setImageResource(R.drawable.certo_gruda1);
            } else { //erro
                setResult(RESULT_CANCELED);
                img.setImageResource(R.drawable.erro_gruda1);
            }

            Toast toast = new Toast(this);
            toast.setView(layoutToast);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();

            finish();
        }
    }
}
