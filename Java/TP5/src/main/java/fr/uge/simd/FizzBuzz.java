package fr.uge.simd;

import jdk.incubator.vector.IntVector;

import java.util.Arrays;

public class FizzBuzz {
    public static int[] fizzBuzzVector128(int length) {
        var species = IntVector.SPECIES_128;
        var loopBound = species.loopBound(length);
        for (int i = 0; i < loopBound; i += species.length()) {

        }
    }

    public static int[] fizzBuzzVector256(int length) {
        var species = IntVector.SPECIES_256;
        var loopBound = species.loopBound(length);
        for (int i = 0; i < loopBound; i += species.length()) {

        }
    }

    public static int[] fizzBuzzVector128AddMask(int length) {
        var species = IntVector.SPECIES_128;
        var loopBound = species.loopBound(length);
        for (int i = 0; i < loopBound; i += species.length()) {

        }
    }

    public static void main(String[] args) {
        /*
        Arrays.stream(fizzBuzz(19)).mapToObj(i -> switch (i) {
            case -1 -> "Fizz";
            case -2 -> "Buzz";
            case -3 -> "FizzBuzz";
            default -> "" + i;
        }).forEach(System.out::println);
         */
    }
}
