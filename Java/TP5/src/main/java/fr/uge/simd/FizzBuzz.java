package fr.uge.simd;

import jdk.incubator.vector.IntVector;

import java.util.Arrays;

public class FizzBuzz {
    private static final int[] VALUES = Arrays.copyOf(new int[] {-3, 1, 2, -1, 4, -2, -1, 7, 8, -1, -2, 11, -1, 13, 14}, 15);
    private static final int[] DELTA = Arrays.copyOf(new int[] {0, 15, 15, 0, 15, 0, 0, 15, 15, 0, 0, 15, 0, 15, 15}, 15);
    public static int[] fizzBuzzVector128(int length) {
        var species = IntVector.SPECIES_128;
        var result = new int[length];
        var speciesLength = species.length();
        var loopBound = length - length % 15;
        var mask15 = species.indexInRange(speciesLength * 3, speciesLength * 3 + 3);    // take 13, 14 and 15 (not 16)
        var maskPost = species.indexInRange(0, 3);  // prevent IndexOutOfBound

        var v1 = IntVector.fromArray(species, VALUES, 0);
        var v2 = IntVector.fromArray(species, VALUES, speciesLength);
        var v3 = IntVector.fromArray(species, VALUES, speciesLength * 2);
        var v4 = IntVector.fromArray(species, VALUES, speciesLength * 3, mask15);

        var d1 = IntVector.fromArray(species, DELTA, 0);
        var d2 = IntVector.fromArray(species, DELTA, speciesLength);
        var d3 = IntVector.fromArray(species, DELTA, speciesLength * 2);
        var d4 = IntVector.fromArray(species, DELTA, speciesLength * 3, mask15);

        int i = 0;
        for (; i < loopBound; i += 3) {
            v1.intoArray(result, i);
            i += speciesLength;
            v2.intoArray(result, i);
            i += speciesLength;
            v3.intoArray(result, i);
            i += speciesLength;
            v4.intoArray(result, i, maskPost);
            v1 = v1.add(d1);
            v2 = v2.add(d2);
            v3 = v3.add(d3);
            v4 = v4.add(d4);
        }
        var mask = species.indexInRange(i, length);
        v1.intoArray(result, i, mask);
        i += mask.trueCount();
        mask = species.indexInRange(i, length);
        v2.intoArray(result, i, mask);
        i += mask.trueCount();
        mask = species.indexInRange(i, length);
        v3.intoArray(result, i, mask);
        i += mask.trueCount();
        mask = species.indexInRange(i, length);
        v4.intoArray(result, i, mask);
        return result;
    }

    public static int[] fizzBuzzVector256(int length) {
        var species = IntVector.SPECIES_256;
        var result = new int[length];
        var speciesLength = species.length();
        var loopBound = length - length % 15;
        var mask15 = species.indexInRange(speciesLength, speciesLength * 2 - 1);    // take 13, 14 and 15 (not 16)
        var maskPost = species.indexInRange(0, speciesLength - 1);  // prevent IndexOutOfBound

        var v1 = IntVector.fromArray(species, VALUES, 0);
        var v2 = IntVector.fromArray(species, VALUES, speciesLength, mask15);

        var d1 = IntVector.fromArray(species, DELTA, 0);
        var d2 = IntVector.fromArray(species, DELTA, speciesLength, mask15);
        int i = 0;
        for (; i < loopBound; i += speciesLength - 1) {
            v1.intoArray(result, i);
            i += speciesLength;
            v2.intoArray(result, i, maskPost);
            v1 = v1.add(d1);
            v2 = v2.add(d2);
        }
        var mask = species.indexInRange(i, length);
        v1.intoArray(result, i, mask);
        i += mask.trueCount();
        mask = species.indexInRange(i, length);
        v2.intoArray(result, i, mask);
        return result;
    }
/*
    public static int[] fizzBuzzVector128AddMask(int length) {
        var species = IntVector.SPECIES_128;
        var loopBound = species.loopBound(length);
        for (int i = 0; i < loopBound; i += species.length()) {

        }
    }

 */
}
