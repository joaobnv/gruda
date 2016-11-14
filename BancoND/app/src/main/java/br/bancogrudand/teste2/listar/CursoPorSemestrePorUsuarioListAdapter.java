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

import br.bancogrudand.DAO.RepositorioUsuarioTemSemestreTemCurso;
import br.bancogrudand.R;
import br.bancogrudand.entidades.Curso;
import br.bancogrudand.entidades.Semestre;
import br.bancogrudand.entidades.Usuario;
import br.bancogrudand.entidades.UsuarioTemSemestreTemCurso;

/**
 * Created by JOÃO BRENO on 24/09/2016.
 */
public class CursoPorSemestrePorUsuarioListAdapter extends BaseAdapter{

    private static final String CATEGORIA_LOG = "CursoPSemPUsuListAdap";
    private Context ctx;
    private List<Curso> lista = new ArrayList<Curso>();

    public CursoPorSemestrePorUsuarioListAdapter(Context ctx, long idUsu, long idSem) {
        Log.i(CATEGORIA_LOG, "Id do usuário: " + idUsu + ". Id do semestre: " + idSem);

        this.ctx = ctx;
        Usuario usu = new Usuario();
        usu.setId(idUsu);

        Semestre sem = new Semestre(idSem, null);

        List<UsuarioTemSemestreTemCurso> listaUSC;

        RepositorioUsuarioTemSemestreTemCurso repUSC = new RepositorioUsuarioTemSemestreTemCurso(ctx);
        listaUSC = repUSC.listar(usu, sem, null);
        repUSC.close();

        Log.i("CursoSemUsuListAdapter", "listaUSC: " + listaUSC.size());

        for(UsuarioTemSemestreTemCurso usc : listaUSC) {
            lista.add(usc.getCurso());
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
        Curso c = lista.get(position);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_listar_curso, null);

        TextView tvDescricao = (TextView) view.findViewById(R.id.tv_curso);
        tvDescricao.setText(c.getDescricao());

        return view;
    }
}
