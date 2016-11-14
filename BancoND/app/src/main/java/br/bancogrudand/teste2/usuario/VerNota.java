package br.bancogrudand.teste2.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.bancogrudand.DAO.RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Nota;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCursoTemDisciplinaTemNota;

/**
 * Created by JOÃO BRENO on 25/09/2016.
 */
public class VerNota extends Activity implements View.OnClickListener{

    private static final int NOVO_EDITAR_NOTA = 1;

    private TextView tvAV1;
    private TextView tvAV2;
    private TextView tvAVF;
    private TextView tvQtdPtPraPassar;
    private TextView tvSituacao;
    private TextView tvMedia;
    private TextView tvMediaFinal;
    private Button btCancelar;
    private Button btEditar;

    private long idUsu, idSem, idCurso, idDisc;

    private Nota nota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_nota);

        tvAV1               = (TextView) findViewById(R.id.tv_av1);
        tvAV2               = (TextView) findViewById(R.id.tv_av2);
        tvAVF               = (TextView) findViewById(R.id.tv_avf);
        tvQtdPtPraPassar = (TextView) findViewById(R.id.tv_qtd_pt_pra_passar);
        tvSituacao          = (TextView) findViewById(R.id.tv_situacao);
        tvMedia             = (TextView) findViewById(R.id.tv_media);
        tvMediaFinal        = (TextView) findViewById(R.id.tv_media_final);

        btCancelar = (Button) findViewById(R.id.bt_cancelar);
        btEditar   = (Button) findViewById(R.id.bt_editar);

        Intent it = getIntent();
        Bundle extras = it.getExtras();
        idUsu   = extras.getLong("idUsu");
        idSem   = extras.getLong("idSem");
        idCurso = extras.getLong("idCurso");
        idDisc  = extras.getLong("idDisc");

        Log.i("verNota", "Usu: " + idUsu + " Sem: " + idSem + " Curso: " + idCurso + " disc: " + idDisc);

        RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN =
                new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(this);
        UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn = repUSCDN.buscar(idUsu, idSem, idCurso, idDisc);
        repUSCDN.close();

        nota = uscdn.getNota();

        if(nota != null) { //nota não é nula
            Double av1, av2, media, avf, mediaFinal;
            av1        = nota.getAv1();
            av2        = nota.getAv2();
            media      = nota.getMedia();
            avf        = nota.getAvf();
            mediaFinal = nota.getMediaFinal();

            if(av1 != null && av2 == null) {

                tvAV1.setText("AV1: " + String.valueOf(av1));

                tvQtdPtPraPassar.setVisibility(View.VISIBLE);
                double ptsPraPassarAV2 = (21 - av1) / 2.;
                double ptsPraPassarAV2Arredondado = Math.ceil(ptsPraPassarAV2 * 100) / 100.;
                tvQtdPtPraPassar.setText(String.format("Precisa de %.2f pontos na AV2 pra passar.", ptsPraPassarAV2Arredondado));

            } else if(av1 != null && av2 != null && media == null) {

                tvAV1.setText("AV1: " + String.valueOf(av1));
                tvAV2.setText("AV2: " + String.valueOf(av2));

                tvMedia.setVisibility(View.VISIBLE);
                tvMedia.setTextColor(0x58b8bcbf);
                double med = (av1 + av2 + av2) / 3.;
                tvMedia.setText(String.format("Média: %.2f", med));

            } else if(av1 != null && av2 != null && media != null && avf == null) {

                tvAV1.setText("AV1: " + String.valueOf(av1));
                tvAV2.setText("AV2: " + String.valueOf(av2));

                tvMedia.setVisibility(View.VISIBLE);
                tvMedia.setText("Média: " + String.valueOf(media));

                tvSituacao.setVisibility(View.VISIBLE);

                if(media >= 7) {

                    tvSituacao.setText("Situação: aprovado!");

                } else if(media < 7 && media >= 4) { //vai pra final

                    tvSituacao.setText("Situação: fazer AVF.");

                    tvQtdPtPraPassar.setVisibility(View.VISIBLE);
                    double ptsPraPassarAVF;
                    if(media >= 5) {
                        ptsPraPassarAVF = 5.;
                    } else {
                        ptsPraPassarAVF = 10 - media;
                    }
                    double ptsPraPassarAVFArredondado = Math.ceil(ptsPraPassarAVF * 100) / 100.;
                    tvQtdPtPraPassar.setText(String.format("Precisa de %.2f pontos na AVF pra passar.", ptsPraPassarAVFArredondado));
                } else {

                    tvSituacao.setText("Situação: reprovado.");

                }

            } else if(av1 != null && av2 != null && media != null && avf != null && mediaFinal == null) {

                tvAV1.setText("AV1: " + String.valueOf(av1));
                tvAV2.setText("AV2: " + String.valueOf(av2));

                tvMedia.setVisibility(View.VISIBLE);
                tvMedia.setText("Média: " + String.valueOf(media));

                tvAVF.setVisibility(View.VISIBLE);
                tvAVF.setText("AVF: " + String.valueOf(avf));

                tvMedia.setVisibility(View.VISIBLE);
                tvMedia.setTextColor(0x58b8bcbf);
                double medFinal = (media + avf) / 2.;
                tvMedia.setText(String.format("Média final: %.2f", medFinal));

            }else if(av1 != null && av2 != null && media != null && avf != null && mediaFinal != null) {

                tvAV1.setText("AV1: " + String.valueOf(av1));
                tvAV2.setText("AV2: " + String.valueOf(av2));

                tvMedia.setVisibility(View.VISIBLE);
                tvMedia.setText("Média: " + String.valueOf(media));

                tvAVF.setVisibility(View.VISIBLE);
                tvAVF.setText("AVF: " + String.valueOf(avf));

                tvMediaFinal.setVisibility(View.VISIBLE);
                tvMediaFinal.setText("Média final: " + mediaFinal);

                tvSituacao.setVisibility(View.VISIBLE);
                if(mediaFinal >= 5 && avf >= 5) {

                    tvSituacao.setText("Situação: aprovado na final!");

                } else {

                    tvSituacao.setText("Situação: reprovado.");

                }

            }
        }

        btCancelar.setOnClickListener(this);
        btEditar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == btCancelar) {
            finish();
        } else if(v == btEditar) {
            Intent it = new Intent(this, novoEditarNota.class);
            Bundle extras = new Bundle();
            extras.putLong("idUsu", idUsu);
            extras.putLong("idSem", idSem);
            extras.putLong("idCurso", idCurso);
            extras.putLong("idDisc", idDisc);
            it.putExtras(extras);
            startActivityForResult(it, NOVO_EDITAR_NOTA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case NOVO_EDITAR_NOTA:
                if(resultCode == RESULT_OK) {
                    atualizarDados();
                }
                break;
            default:
                //nada
        }
    }

    private void atualizarDados() {
        Nota n;
        UsuarioTemSemestreTemCursoTemDisciplinaTemNota uscdn;
        RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota repUSCDN =
                new RepositorioUsuarioTemSemestreTemCursoTemDisciplinaTemNota(this);
        uscdn = repUSCDN.buscar(idUsu, idSem, idCurso, idDisc);
        repUSCDN.close();

        n = uscdn.getNota();
        if(n != null) {
            Double av1, av2;
            av1 = n.getAv1();
            av2 = n.getAv2();
            if(av1 != null) {
                tvAV1.setText(String.valueOf(av1));
            }
            if(av2 != null) {
                tvAV2.setText(String.valueOf(av2));
            }
            Log.i("VerNota", "nota != null");
        } else {
            tvAV1.setText("");
            tvAV2.setText("");
            Log.i("VerNota", "nota == null");
        }
    }

}
