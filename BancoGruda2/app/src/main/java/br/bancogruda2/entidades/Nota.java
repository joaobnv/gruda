package br.bancogruda2.entidades;

/**
 * Created by JOÃO BRENO on 31/08/2016.
 */
public class Nota {

    //nome da tabela no banco de dados correspondente a essa classe
    public static final String NOME_DA_TABELA = "nota";

    //nome das colunas da tabela no banco de dados
    public static final String ID = "id";
    public static final String AV1 = "av1";
    public static final String AV2 = "av2";

    private long id;
    //é da classe empacotadora porquê pode ser null, 0 para null é um valor inconsistente nesse caso
    //porque a nota pode ser 0
    private Double av1;
    //é da classe empacotadora porquê pode ser null, 0 para null é um valor inconsistente nesse caso
    //porque a nota pode ser 0
    private Double av2;
    private double mediaFinal;
    private String situacao;

    public Nota() {
    }

    public Nota(Double av1, Double av2) {
        setAv1(av1);
        setAv2(av2);
    }

    public Nota(long id, Double av1, Double av2) {
        setId(id);
        setAv1(av1);
        setAv2(av2);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if(id >= 0)
            this.id = id;
        else
            throw new IllegalArgumentException("O id deve ser >= 0.");
    }

    public Double getAv1() {
        return av1;
    }

    public void setAv1(Double av1) {
        if(av1 < 0 || av1 > 10) {
            throw new IllegalArgumentException("O valor do argumento deve ser >0 e <=10.");
        }
        this.av1 = av1;
        setMediaFinal();
    }

    public Double getAv2() {
        return av2;
    }

    public void setAv2(Double av2) {
        if(av2 < 0 || av2 > 10) {
            throw new IllegalArgumentException("O valor do argumento deve ser >0 e <=10.");
        }
        this.av2 = av2;
        setMediaFinal();
    }

    public double getMediaFinal() {
        return mediaFinal;
    }

    private void setMediaFinal() {
        //é considerado que av1 e av2 têm valores consistentes
        mediaFinal = (av1 + av2 + av2) / 3;
        setSituacao();
    }

    public String getSituacao() {
        return situacao;
    }

    private void setSituacao(){
        //é considerado que mediaFinal tem um valor consistente
        if(mediaFinal < 4) {
            situacao = "Reprovado";
        } else if(mediaFinal < 7) {
            situacao = "Fazer AVF";
        } else {
            situacao = "Aprovado";
        }
    }

    @Override
    public boolean equals(Object o) { //só analisa o id
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nota nota = (Nota) o;

        return id == nota.id;

    }

    @Override
    public int hashCode() { //só analisa o id
        return (int) (id ^ (id >>> 32));
    }
}
