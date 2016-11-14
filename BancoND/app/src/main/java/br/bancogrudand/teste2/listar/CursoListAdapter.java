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

import br.bancogrudand.DAO.RepositorioCurso;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Curso;

/**
 * Created by JOÃO BRENO on 25/09/2016.
 */
public class CursoListAdapter extends BaseAdapter {

    private static final String CATEGORIA_LOG = "CursoListAdapter";
    private Context ctx;
    private List<Curso> lista = new ArrayList<Curso>();

    public CursoListAdapter(Context ctx) {
        this.ctx = ctx;

        RepositorioCurso repCurso = new RepositorioCurso(ctx);
        lista = repCurso.listar();
        repCurso.close();

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
        Curso c = lista.get(position);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_listar_curso, null);

        TextView tvCurso = (TextView) view.findViewById(R.id.tv_curso);
        tvCurso.setText(c.getDescricao());

        return view;
    }
}