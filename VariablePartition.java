import java.util.ArrayList;
import java.util.List;

public class VariablePartition {

    public enum Policy {
        WORST_FIT, CIRCULAR_FIT
    }

    private final Policy policy;
    private final List<Block> memory;
    private int circularIndex;

    public VariablePartition(int totalSize, Policy policy) {
        this.policy = policy;
        this.memory = new ArrayList<>();
        this.circularIndex = 0;
        memory.add(new Block(0, totalSize, null));
    }

    public void allocate(String processId, int size) {
        System.out.println("\n-> IN " + processId + " (" + size + " KB)");

        for (Block b : memory) {
            if (processId.equals(b.processId)) {
                System.out.println(" ERRO: Processo " + processId + " já está na memória.");
                printState();
                return;
            }
        }

        if (policy == Policy.WORST_FIT) {
            allocateWorstFit(processId, size);
        } else {
            allocateCircularFit(processId, size);
        }
    }

    private void allocateWorstFit(String processId, int size) {
        int worstIdx = -1;
        int worstSize = -1;

        for (int i = 0; i < memory.size(); i++) {
            Block b = memory.get(i);
            if (b.isFree() && b.size >= size) {
                if (b.size > worstSize) {
                    worstSize = b.size;
                    worstIdx = i;
                }
            }
        }

        if (worstIdx == -1) {
            System.out.println("ESPAÇO INSUFICIENTE DE MEMÓRIA");
        } else {
            split(worstIdx, processId, size);
        }
        printState();
    }

    private void allocateCircularFit(String processId, int size) {
        int n = memory.size();
        int startSearch = circularIndex % n;
        int foundIdx = -1;

        for (int i = 0; i < n; i++) {
            int idx = (startSearch + i) % n;
            Block b = memory.get(idx);
            if (b.isFree() && b.size >= size) {
                foundIdx = idx;
                break;
            }
        }

        if (foundIdx == -1) {
            System.out.println("ESPAÇO INSUFICIENTE DE MEMÓRIA");
        } else {
            split(foundIdx, processId, size);
            circularIndex = foundIdx + 1;
            if (circularIndex >= memory.size())
                circularIndex = 0;
        }
        printState();
    }

    private void split(int idx, String processId, int size) {
        Block b = memory.get(idx);
        int remainder = b.size - size;

        b.processId = processId;
        b.size = size;

        if (remainder > 0) {
            memory.add(idx + 1, new Block(b.start + size, remainder, null));
        }
        System.out.println(" Alocado: " + processId + " em " + b.start + "-" + (b.start + size - 1));
    }

    public void free(String processId) {
        System.out.println("\n-> OUT " + processId);

        int idx = -1;
        for (int i = 0; i < memory.size(); i++) {
            if (processId.equals(memory.get(i).processId)) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            System.out.println(" ERRO: Processo " + processId + " não encontrado na memória.");
            printState();
            return;
        }

        memory.get(idx).processId = null;
        System.out.println(" Liberado: " + processId);

        if (idx + 1 < memory.size() && memory.get(idx + 1).isFree()) {
            Block right = memory.remove(idx + 1);
            memory.get(idx).size += right.size;
        }

        if (idx - 1 >= 0 && memory.get(idx - 1).isFree()) {
            Block left = memory.get(idx - 1);
            left.size += memory.get(idx).size;
            memory.remove(idx);
            idx--;
        }

        if (policy == Policy.CIRCULAR_FIT && circularIndex >= memory.size()) {
            circularIndex = 0;
        }

        printState();
    }

    public void printState() {
        System.out.println(" Estado da memória:");

        StringBuilder map = new StringBuilder(" | ");
        for (Block b : memory) {
            String label = b.isFree() ? "LIVRE" : b.processId;
            int width = Math.max(label.length() + 2, 4);
            map.append(center(label, width)).append(" | ");
        }
        System.out.println(map);

        int freeBlocks = 0;
        int freeTotal = 0;
        for (Block b : memory) {
            String status = b.isFree() ? "LIVRE" : "OCUPADO (" + b.processId + ")";
            System.out.printf(" [%3d - %3d] %4d KB  %s%n", b.start, b.start + b.size - 1, b.size, status);
            if (b.isFree()) {
                freeBlocks++;
                freeTotal += b.size;
            }
        }
        System.out.println(" Blocos contíguos livres: " + freeBlocks + " | Total livre: " + freeTotal + " KB");
    }

    private static String center(String s, int width) {
        if (s.length() >= width)
            return s.substring(0, width);
        int pad = width - s.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}