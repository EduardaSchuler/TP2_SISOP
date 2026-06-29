public class Block {
    public int start;
    public int size;
    public String processId;

    public Block(int start, int size, String processId) {
        this.start = start;
        this.size = size;
        this.processId = processId;
    }

    public boolean isFree() {
        return processId == null;
    }

    @Override
    public String toString() {
        return start + "-" + (start + size - 1) + " | " + size + " | " + (isFree() ? "LIVRE" : processId);
    }
}