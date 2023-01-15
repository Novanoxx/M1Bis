package fr.uge.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class VectorComputation {
    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
    public static int sum(int[] array) {
        var v1 = IntVector.zero(SPECIES);
        var length = array.length;
        var loopBound = length - length % SPECIES.length();
        int i = 0;
        for(; i < loopBound; i += SPECIES.length()) {
            var v2 = IntVector.fromArray(SPECIES, array, i);
            v1 = v1.add(v2);
        }
        var sum = v1.reduceLanes(VectorOperators.ADD);
        for(; i < length; i++) { // post loop
            sum += array[i];
        }
        return sum;
    }

    public static int sumMask(int[] array) {
        var v1 = IntVector.zero(SPECIES);
        var length = array.length;
        var loopBound = length - length % SPECIES.length();
        int i = 0;
        for(; i < loopBound; i += SPECIES.length()) {
            var v2 = IntVector.fromArray(SPECIES, array, i);
            v1 = v1.add(v2);
        }
        var mask = SPECIES.indexInRange(i, length);
        var mask_v = IntVector.fromArray(SPECIES, array, i, mask);
        v1 = v1.add(mask_v);
        return v1.reduceLanes(VectorOperators.ADD);
    }

    public static int min(int[] array) {
        var v1 = IntVector.broadcast(SPECIES, Integer.MAX_VALUE);
        var length = array.length;
        var loopBound = SPECIES.loopBound(length);
        var i = 0;
        for(; i < loopBound; i += SPECIES.length()) {
            var v2 = IntVector.fromArray(SPECIES, array, i);
            v1 = v1.min(v2);
        }
        for(; i < length; i++) { // post loop
            v1 = v1.min(array[i]);
        }
        return v1.reduceLanes(VectorOperators.MIN);
    }

    public static int minMask(int[] array) {
        var v1 = IntVector.broadcast(SPECIES, Integer.MAX_VALUE);
        var length = array.length;
        var loopBound = SPECIES.loopBound(length);
        var i = 0;
        for(; i < loopBound; i += SPECIES.length()) {
            var v2 = IntVector.fromArray(SPECIES, array, i);
            v1 = v1.min(v2);
        }
        var mask = SPECIES.indexInRange(i, length);
        var mask_v = IntVector.fromArray(SPECIES,array, i, mask);
        v1 = v1.lanewise(VectorOperators.MIN, mask_v, mask);
        return v1.reduceLanes(VectorOperators.MIN);
    }
}
