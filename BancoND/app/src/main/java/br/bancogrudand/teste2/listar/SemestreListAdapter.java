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

import br.bancogrudand.DAO.RepositorioSemestre;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Semestre;

/**
 * Created by JOÃO BRENO on 25/09/2016.
 */
public class SemestreListAdapter extends BaseAdapter {

    public static final String CATEGORIA_LOG = "SemestreListAdapter";

    private Context ctx;
    private List<Semestre> lista = new ArrayList<Semestre>();

    public SemestreListAdapter(Context ctx) {
        this.ctx = ctx;

        RepositorioSemestre repSem = new RepositorioSemestre(ctx);
        lista = repSem.listar();
        repSem.close();

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
        Semestre s = lista.get(position);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_listar_semestre, null);

        TextView tvDescricao = (TextView) view.findViewById(R.id.tv_descricao);
        tvDescricao.setText(s.getDescricao());

        return view;
    }
}