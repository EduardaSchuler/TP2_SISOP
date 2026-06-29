import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        String filePath = "";
        File file = null;
        while (file == null || !file.exists()) {
            System.out.print("Informe o caminho do arquivo de requisições: ");
            filePath = sc.nextLine().trim();
            file = new File(filePath);
            if (!file.exists()) System.out.println("ERRO: Arquivo não encontrado.");
        }

        int memSize = 0;
        while (!elevadoDois(memSize)) {
            System.out.print("Informe o tamanho da memória principal (potência de 2, em KB): ");
            try { memSize = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { memSize = 0; }
            if (!elevadoDois(memSize)) System.out.println("  ERRO: Deve ser uma potência de 2");
        }

        boolean continuar = true;
        while (continuar) {
            System.out.println("Escolha a estratégia de alocação:");
            System.out.println("1 - Partições Variáveis");
            System.out.println("2 - Sistema Buddy");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            String estrategia = sc.nextLine().trim();

            switch (estrategia) {
                case "1":
                    System.out.println("Escolha a política de alocação:");
                    System.out.println("1 - Worst-Fit");
                    System.out.println("2 - Circular-Fit");
                    System.out.print("Opção: ");
                    String politica = sc.nextLine().trim();

                    VariablePartition.Policy policy = politica.equals("2")
                            ? VariablePartition.Policy.CIRCULAR_FIT
                            : VariablePartition.Policy.WORST_FIT;

                    String policyName = policy == VariablePartition.Policy.WORST_FIT ? "Worst-Fit" : "Circular-Fit";
                    System.out.println("Partições Variáveis | Política: " + policyName + " | Memória: " + memSize + " KB ---");

                    VariablePartition vp = new VariablePartition(memSize, policy);
                    System.out.println("[Estado inicial]");
                    vp.printState();
                    processFile(file, vp, null);
                    break;

                case "2":
                    System.out.println("Sistema Buddy | Memória: " + memSize + " KB ---");
                    BuddySystem buddy = new BuddySystem(memSize);
                    System.out.println("[Estado inicial]");
                    buddy.printState();
                    processFile(file, null, buddy);
                    break;

                case "0":
                    continuar = false;
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }

        System.out.println("Processamento concluído.");
        sc.close();
    }

    private static void processFile(File file, VariablePartition vp, BuddySystem buddy) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int lineNum = 0;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            lineNum++;

            String normalized = line
                    .replaceAll("\\(", " ")
                    .replaceAll("\\)", "")
                    .replaceAll(",", " ")
                    .replaceAll("\\s+", " ")
                    .trim();

            String[] parts = normalized.split(" ");
            if (parts.length < 2) {
                System.out.println("Linha " + lineNum + " inválida: " + line);
                continue;
            }

            String command = parts[0].toUpperCase();
            String processId = parts[1];

            switch (command) {
                case "IN":
                    if (parts.length < 3) {
                        System.out.println("Linha " + lineNum + " inválida (IN sem tamanho): " + line);
                        continue;
                    }
                    int size;
                    try { size = Integer.parseInt(parts[2]); }
                    catch (NumberFormatException e) {
                        System.out.println("Linha " + lineNum + ": tamanho inválido: " + parts[2]);
                        continue;
                    }
                    if (vp != null) vp.allocate(processId, size);
                    else buddy.allocate(processId, size);
                    break;

                case "OUT":
                    if (vp != null) vp.free(processId);
                    else buddy.free(processId);
                    break;

                default:
                    System.out.println("Linha " + lineNum + ": comando desconhecido '" + command + "'");
            }
        }
        br.close();
    }

    private static boolean elevadoDois(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
}