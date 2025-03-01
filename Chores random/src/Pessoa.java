public class Pessoa implements Elemento {
    private String nome;
    private int pontos;

    public Pessoa(String nome, int pontos) {
        this.nome = nome;
        this.pontos = pontos;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int getPontos() {
        return pontos;
    }
}
