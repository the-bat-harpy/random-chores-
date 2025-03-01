public class Tarefa {
    private String nome;
    private int pontos;
    private String excluidos;
    private boolean atribuida; // Novo atributo para indicar se a tarefa foi atribuída

    public Tarefa(String nome, int pontos, String excluidos) {
        this.nome = nome;
        this.pontos = pontos;
        this.excluidos = excluidos;
        this.atribuida = false; // Inicializa como não atribuída
    }

    public String getNome() {
        return nome;
    }

    public int getPontos() {
        return pontos;
    }

    public String getExcluidos() {
        return excluidos;
    }

    public boolean isAtribuida() {
        return atribuida; // Getter para o novo atributo
    }

    public void setAtribuida(boolean atribuida) {
        this.atribuida = atribuida; // Setter para o novo atributo
    }
}
