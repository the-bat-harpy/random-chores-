public class Main {
    public static void main(String[] args) {
        // Nome do arquivo de entrada (modifique o caminho conforme necessário)
        String inputFilename = "infos.txt"; // O arquivo com os dados de entrada
        String outputFilename = "Atribuicao.txt"; // O arquivo onde a atribuição será salva
        
        // Cria uma instância da classe Atribuir
        Atribuir atribuir = new Atribuir(inputFilename, outputFilename);

        // Atribui tarefas com base nos dados do arquivo
        atribuir.atribuirTarefas();

        // Escreve a atribuição em um arquivo
        atribuir.escreverFicheiro();
    }
}
