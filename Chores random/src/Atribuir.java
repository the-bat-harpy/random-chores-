import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Atribuir {
    private ArrayList<Pessoa> pessoas;
    private ArrayList<Tarefa> tarefas;
    private ArrayList<Node> atribuicao;  
    private String filename;
    private String endfilename;

    public Atribuir(String fileInicial, String fileFinal) {
        this.pessoas = new ArrayList<>();
        this.tarefas = new ArrayList<>();
        this.atribuicao = new ArrayList<>();  
        this.filename = fileInicial;
        this.endfilename = fileFinal;
    }
    

    public void recolherData() {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                String[] data = linha.split(" ");  
                
                if (data[0].equals("N")) {
                    String nome = data[1];
                    int pontos = Integer.parseInt(data[2]);
                    pessoas.add(new Pessoa(nome, pontos));
                } else if (data[0].equals("T")) {
                    String nome = data[1];
                    int pontos = Integer.parseInt(data[2]);
                    String excluidos = (data.length > 3) ? data[3] : "";
                    tarefas.add(new Tarefa(nome, pontos, excluidos));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        }
    }

    public int calcularPontosTotais() {
        int pontos = 0;
        for (Tarefa t : tarefas) {
            pontos += t.getPontos();
        }
        return pontos;
    }

    public int calcularPontosPorPessoa() {
        int pontosTotais = calcularPontosTotais();
        int numeroPessoas = pessoas.size();
        
        if (numeroPessoas == 0) {
            System.out.println("Não há pessoas para atribuir tarefas.");
            return 0;
        }

        return pontosTotais / numeroPessoas;  // Retorna a média
    }

    public void atribuirTarefas() {
        recolherData(); // Coleta dados de pessoas e tarefas

        // Inicializa os nodes para cada pessoa
        for (Pessoa p : pessoas) {
            String nome = p.getNome();
            Node atual = new Node(nome, 0);
            atribuicao.add(atual);
        }

        Random random = new Random();

        // Priorizar tarefas exclusivas
        for (Tarefa tarefa : tarefas) {
            if (!tarefa.getExcluidos().isEmpty()) {
                String[] excluidos = tarefa.getExcluidos().split(",");
                List<Node> elegiveis = new ArrayList<>();
                for (Node pessoa : atribuicao) {
                    if (!isExcluded(pessoa.getNome(), excluidos)) {
                        elegiveis.add(pessoa);
                    }
                }

                if (!elegiveis.isEmpty()) {
                    // Escolher uma pessoa aleatória dos elegíveis
                    Node pessoaElegivel = elegiveis.get(random.nextInt(elegiveis.size()));
                    pessoaElegivel.adicionarTarefa(tarefa);
                    tarefa.setAtribuida(true);
                    System.out.println("Atribuindo tarefa " + tarefa.getNome() + " a " + pessoaElegivel.getNome());
                }
            }
        }

        // Restante das tarefas
        List<Tarefa> tarefasRestantes = new ArrayList<>();
        for (Tarefa tarefa : tarefas) {
            if (!tarefa.isAtribuida()) {
                tarefasRestantes.add(tarefa);
            }
        }

        // Ordenar tarefas restantes por pontos (maior prioridade primeiro)
        tarefasRestantes.sort((t1, t2) -> Integer.compare(t2.getPontos(), t1.getPontos()));

        // Atribuir tarefas restantes de forma aleatória
        for (Tarefa tarefa : tarefasRestantes) {
            List<Node> elegiveis = new ArrayList<>();
            for (Node pessoa : atribuicao) {
                if (!isExcluded(pessoa.getNome(), tarefa.getExcluidos().split(","))) {
                    elegiveis.add(pessoa);
                }
            }

            if (!elegiveis.isEmpty()) {
                // Escolher uma pessoa aleatória dos elegíveis
                Node pessoaElegivel = elegiveis.get(random.nextInt(elegiveis.size()));
                pessoaElegivel.adicionarTarefa(tarefa);
                tarefa.setAtribuida(true);
                System.out.println("Atribuindo tarefa " + tarefa.getNome() + " a " + pessoaElegivel.getNome());
            }
        }

        // Redistribuir tarefas para balancear
        redistribuirTarefas();

        // Exibir situação atual
        exibirSituacaoAtual();

        // Escrever atribuições em arquivo
        escreverFicheiro();
    }

    private void redistribuirTarefas() {
        boolean mudou = true;
        while (mudou) {
            mudou = false;
            atribuicao.sort((n1, n2) -> Integer.compare(n2.getPontos(), n1.getPontos())); // Mais pontos primeiro
            Node maisCarregado = atribuicao.get(0);
            Node menosCarregado = atribuicao.get(atribuicao.size() - 1);

            // Procurar uma tarefa genérica para realocar
            for (Tarefa tarefa : maisCarregado.getTarefas()) {
                if (tarefa.getExcluidos().isEmpty()) { // Tarefa genérica
                    maisCarregado.getTarefas().remove(tarefa);
                    maisCarregado.setPontos(maisCarregado.getPontos() - tarefa.getPontos());
                    menosCarregado.adicionarTarefa(tarefa);
                    System.out.println("Realocando tarefa " + tarefa.getNome() + " de " + maisCarregado.getNome() +
                            " para " + menosCarregado.getNome());
                    mudou = true;
                    break;
                }
            }
        }
    }


    private boolean isExcluded(String nomePessoa, String[] excluidos) {
        for (String excluido : excluidos) {
            if (nomePessoa.equals(excluido.trim())) {
                return true;
            }
        }
        return false;
    }

    public void exibirSituacaoAtual() {
        System.out.println("Situação de atribuições:");
        for (Node pessoa : atribuicao) {
            System.out.println(pessoa);
        }
    }

    // Método para escrever a atribuição em arquivo
    public void escreverFicheiro() {
        try {
            PrintWriter writer = new PrintWriter(new File(endfilename));
            for (Node n : atribuicao) {
                writer.println(n);
            }
            writer.close();
            System.out.println("Arquivo de saída criado com sucesso: " + endfilename);
        } catch (FileNotFoundException e) {
            System.err.println("Problem while writing to the file: " + endfilename);
        }
    }

    // Classe Node permanece a mesma
    public class Node {
        private String nome;
        private int pontos;
        private List<Tarefa> tarefas;

        public Node(String nome, int pontos) {
            this.nome = nome;
            this.pontos = pontos;
            this.tarefas = new ArrayList<>();
        }

        public String getNome() {
            return nome;
        }

        public int getPontos() {
            return pontos;
        }

        public List<Tarefa> getTarefas() {
            return tarefas;
        }

        public void adicionarTarefa(Tarefa tarefa) {
            this.tarefas.add(tarefa);
            this.pontos += tarefa.getPontos();  // Atualiza os pontos ao adicionar a tarefa
        }

        public void removerTarefa(Tarefa tarefa) {
            this.tarefas.remove(tarefa);
            this.pontos -= tarefa.getPontos();  // Atualiza os pontos ao remover a tarefa
        }

        public void setPontos(int pontos) {
            this.pontos = pontos;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(nome + ": " + pontos + " pontos | Tarefas: ");
            for (Tarefa t : tarefas) {
                sb.append(t.getNome()).append(" ");
            }
            return sb.toString().trim();
        }
    }

}
