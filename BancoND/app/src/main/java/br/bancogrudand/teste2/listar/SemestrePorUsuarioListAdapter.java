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

import br.bancogrudand.DAO.RepositorioUsuarioTemSemestre;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Semestre;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.entidades.UsuarioTemSemestre;

/**
 * Created by JOÃO BRENO on 18/09/2016.
 */
public class SemestrePorUsuarioListAdapter extends BaseAdapter {

    private static final String CATEGORIA_LOG = "SemPorUsuListAdapter";
    private Context ctx;
    private List<Semestre> lista = new ArrayList<Semestre>();

    public SemestrePorUsuarioListAdapter(Context ctx, long idUsu) {
        Log.i(CATEGORIA_LOG, "Id do usuário: " + idUsu);
        this.ctx = ctx;

        Usuario porUsuario = new Usuario();
        porUsuario.setId(idUsu);

        RepositorioUsuarioTemSemestre repUsuTemSem = new RepositorioUsuarioTemSemestre(ctx);
        List<UsuarioTemSemestre> lUS = repUsuTemSem.listar(porUsuario, null);
        repUsuTemSem.close();

        for (UsuarioTemSemestre usuTemSem : lUS) {
            lista.add(usuTemSem.getSemestre());
        }

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
