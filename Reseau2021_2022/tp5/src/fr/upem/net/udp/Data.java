package fr.upem.net.udp;

import java.util.BitSet;

public class Data {
    private final BitSet bitset;
    private final long nbOp;
    private int count = 0;
    private Long sum = 0L;

    public Data(long nbOp) {
        if (nbOp < 0L) {
            throw new IllegalArgumentException("Number of Operation must be greater than 0");
        }
        this.nbOp = nbOp;
        this.bitset = new BitSet((int)nbOp);
    }

    public long sum() {
        return sum;
    }

    public void addSum(long id, long opValue) {
        synchronized (bitset) {
            if (!bitset.get((int) id)) {
                bitset.set((int) id);
                sum += opValue;
                count++;
            }
        }
    }

    boolean isFinished() {
        synchronized (bitset) {
            return count == nbOp;
        }
    }
}
