package br.bancogrudand.teste2.listar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.bancogrudand.DAO.RepositorioDisciplina;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Curso;
import br.bancogrudand.entidades.Disciplina;

/**
 * Created by JOÃO BRENO on 25/09/2016.
 */
public class DisciplinaPorCursoListAdapter extends BaseAdapter {

    private static final String CATEGORIA_LOG = "DiscPorCursoListAdap";
    private Context ctx;
    private List<Disciplina> lista = new ArrayList<Disciplina>();

    public DisciplinaPorCursoListAdapter(Context ctx, long idCurso) {
        Log.i(CATEGORIA_LOG, "Id do curso: " + idCurso);
        this.ctx = ctx;

        Curso porCurso = new Curso(idCurso, null);

        RepositorioDisciplina repDisc = new RepositorioDisciplina(ctx);
        lista = repDisc.listar(porCurso);
        repDisc.close();

        Log.i(CATEGORIA_LOG, "Tamanho da lista: " + lista.size() + " items.");
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Disciplina d = lista.get(position);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_listar_disciplina, null);

        TextView tvDisciplina = (TextView) view.findViewById(R.id.tv_disciplina);
        tvDisciplina.setText(d.getDescricao());

        return view;
    }
}