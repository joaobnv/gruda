package br.bancogrudand.entidades;

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
    public static final String MEDIA = "media";
    public static final String AVF = "avf";
    public static final String MEDIA_FINAL = "media_final";

    private long id;
    private Double av1;
    private Double av2;
    private Double media;
    private Double avf;
    private Double mediaFinal;

    public Nota() {
    }

    public Nota(Double av1, Double av2, Double media, Double avf, Double mediaFinal) {
        setAv1(av1);
        setAv2(av2);
        setAvf(avf);
        setMedia(media);
        setMediaFinal(mediaFinal);
    }

    public Nota(long id, Double av1, Double av2, Double media, Double avf, Double mediaFinal) {
        setId(id);
        setAv1(av1);
        setAv2(av2);
        setAvf(avf);
        setMedia(media);
        setMediaFinal(mediaFinal);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAv1(Double av1) {
        this.av1 = av1;
    }

    public void setAv2(Double av2) {
        this.av2 = av2;
    }

    public void setMedia(Double media) {
        this.media = media;
    }

    public void setAvf(Double avf) {
        this.avf = avf;
    }

    public void setMediaFinal(Double mediaFinal) {
        this.mediaFinal = mediaFinal;
    }

    public long getId() {
        return id;
    }

    public Double getAv1() {
        return av1;
    }

    public Double getAv2() {
        return av2;
    }

    public Double getMedia() {
        return media;
    }

    public Double getAvf() {
        return avf;
    }

    public Double getMediaFinal() {
        return mediaFinal;
    }

    @Override
    public boolean equals(Object o) { //só analisa o id
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Nota nota = (Nota) o;

        return id == nota.id;

    }

    @Override
    public int hashCode() { //só analisa o id
        return (int) (id ^ (id >>> 32));
    }
}
