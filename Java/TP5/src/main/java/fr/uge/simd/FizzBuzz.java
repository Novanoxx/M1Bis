package fr.uge.simd;

import jdk.incubator.vector.IntVector;

import java.util.Arrays;

public class FizzBuzz {
    public static int[] fizzBuzzVector128(int longueur) {
        var species = IntVector.SPECIES_128;
    }

    public static int[] fizzBuzzVector256(int longueur) {
        var species = IntVector.SPECIES_256;
    }

    public static int[] fizzBuzzVector128AddMask(int longueur) {
        var species = IntVector.SPECIES_128;
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
