import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BuddySystem {

    static class BuddyBlock {
        int start;
        int size;
        String processId;
        int allocatedSize;

        BuddyBlock(int start, int size) {
            this.start = start;
            this.size = size;
            this.processId = null;
            this.allocatedSize = 0;
        }

        boolean isFree() {
            return processId == null;
        }
    }

    private final int totalSize;
    private final TreeMap<Integer, List<BuddyBlock>> freeLists;
    private final Map<String, BuddyBlock> allocated;

    public BuddySystem(int totalSize) {
        if (!elevadoDois(totalSize)) {
            throw new IllegalArgumentException("Tamanho deve ser potência de 2");
        }
        this.totalSize = totalSize;
        this.freeLists = new TreeMap<>();
        this.allocated = new HashMap<>();

        List<BuddyBlock> list = new ArrayList<>();
        list.add(new BuddyBlock(0, totalSize));
        freeLists.put(totalSize, list);
    }

    private int proximaPotencia(int n) {
        if (n <= 1)
            return 1;
        int p = 1;
        while (p < n)
            p <<= 1;
        return p;
    }

    private boolean elevadoDois(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    public void allocate(String processId, int requestedSize) {
        System.out.println("\n-> IN " + processId + " (" + requestedSize + " KB)");

        if (allocated.containsKey(processId)) {
            System.out.println(" ERRO: Processo " + processId + " já está na memória.");
            printState();
            return;
        }

        int blockSize = proximaPotencia(requestedSize);
        System.out.println(" Tamanho requisitado: " + requestedSize + " KB -> bloco alocado: " + blockSize + " KB");

        int totalFree = 0;
        for (Map.Entry<Integer, List<BuddyBlock>> e : freeLists.entrySet()) {
            totalFree += e.getKey() * e.getValue().size();
        }
        if (totalFree < blockSize) {
            System.out.println(" ESPAÇO INSUFICIENTE DE MEMÓRIA");
            printState();
            return;
        }

        Integer foundSize = null;
        for (Integer sz : freeLists.keySet()) {
            if (sz >= blockSize && !freeLists.get(sz).isEmpty()) {
                foundSize = sz;
                break;
            }
        }

        if (foundSize == null) {
            System.out.println(" ESPAÇO INSUFICIENTE DE MEMÓRIA");
            printState();
            return;
        }

        BuddyBlock block = freeLists.get(foundSize).remove(0);
        if (freeLists.get(foundSize).isEmpty())
            freeLists.remove(foundSize);

        while (block.size > blockSize) {
            int half = block.size / 2;
            BuddyBlock buddy = new BuddyBlock(block.start + half, half);
            block.size = half;

            freeLists.computeIfAbsent(half, k -> new ArrayList<>()).add(buddy);
        }

        block.processId = processId;
        block.allocatedSize = requestedSize;
        allocated.put(processId, block);

        int fragmentation = blockSize - requestedSize;
        System.out.println(" Alocado: " + processId + " em " + block.start + "-" + (block.start + blockSize - 1)
                + " Fragmentação interna: " + fragmentation + "KB");
        printState();
    }

    public void free(String processId) {
        System.out.println("\n-> OUT " + processId);

        BuddyBlock block = allocated.remove(processId);
        if (block == null) {
            System.out.println("   ERRO: Processo " + processId + " não encontrado na memória.");
            printState();
            return;
        }

        block.processId = null;
        block.allocatedSize = 0;
        System.out.println(" Liberado: " + processId + " bloco " + block.start + "-" + (block.start + block.size - 1));
        coalesce(block);
        printState();
    }

    private void coalesce(BuddyBlock block) {
        while (block.size < totalSize) {
            int buddyStart = getBuddyStart(block.start, block.size);
            BuddyBlock buddy = findFreeBlock(buddyStart, block.size);

            if (buddy == null)
                break;

            List<BuddyBlock> list = freeLists.get(block.size);
            if (list != null)
                list.remove(buddy);
            if (list != null && list.isEmpty())
                freeLists.remove(block.size);

            int mergedStart = Math.min(block.start, buddyStart);
            block = new BuddyBlock(mergedStart, block.size * 2);
            System.out.println(
                    "   Coalescência: blocos unidos em " + mergedStart + "-" + (mergedStart + block.size - 1));
        }

        freeLists.computeIfAbsent(block.size, k -> new ArrayList<>()).add(block);
    }

    private int getBuddyStart(int start, int size) {
        return start ^ size;
    }

    private BuddyBlock findFreeBlock(int start, int size) {
        List<BuddyBlock> list = freeLists.get(size);
        if (list == null)
            return null;
        for (BuddyBlock b : list) {
            if (b.start == start)
                return b;
        }
        return null;
    }

    public void printState() {
        System.out.println(" Estado da memória (Buddy System):");

        List<BuddyBlock> all = new ArrayList<>();
        for (List<BuddyBlock> list : freeLists.values())
            all.addAll(list);
        all.addAll(allocated.values());
        all.sort(Comparator.comparingInt(b -> b.start));

        int freeBlocks = 0;
        int freeTotal = 0;
        int internalFrag = 0;

        for (BuddyBlock b : all) {
            if (b.isFree()) {
                System.out.printf(" [%3d - %3d] %4d KB  LIVRE%n", b.start, b.start + b.size - 1, b.size);
                freeBlocks++;
                freeTotal += b.size;
            } else {
                int frag = b.size - b.allocatedSize;
                System.out.printf(" [%3d - %3d] %4d KB  OCUPADO (%s) | req: %d KB | frag. interna: %d KB%n", b.start, b.start + b.size - 1, b.size, b.processId, b.allocatedSize, frag);
                internalFrag += frag;
            }
        }
        
        System.out.println(" Blocos contíguos livres: " + freeBlocks + " | Total livre: " + freeTotal + " KB");
        System.out.println(" Fragmentação interna total: " + internalFrag + " KB");
    }
}