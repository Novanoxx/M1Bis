### VONG Stéphane (groupe apprenti 2)
# TP5 Java 2022-2023
## Exercice 2 - Vectorized Add / Vectorized Min

1. On cherche à écrire une fonction sum qui calcule la somme des entiers d'un tableau passé en paramètre. Pour cela, nous allons utiliser l'API de vectorisation pour calculer la somme sur des vecteurs.
   <br>- _Quelle est la classe qui représente des vecteurs d'entiers ?_
   <br>- _Qu'est ce qu'un VectorSpecies et quelle est la valeur de VectorSpecies que nous allons utiliser dans notre cas ?_
   <br>- _Comment créer un vecteur contenant des zéros et ayant un nombre préféré de lanes ?_
   <br>- _Comment calculer la taille de la boucle sur les vecteurs (loopBound) ?_
   <br>- _Comment faire la somme de deux vecteurs d'entiers ?_
   <br>- _Comment faire la somme de toutes les lanes d'un vecteur d'entiers ?_
   <br>- _Si la longueur du tableau n'est pas un multiple du nombre de lanes, on va utiliser une post-loop, quel doit être le code de la post-loop ?_

   Une fois que vous avez répondu à toutes ces questions, écrire le code de sum et vérifier que le test nommé "testSum" passe. De plus, vérifier avec les tests de performance dans VectorComputationBenchMark (dé-commenter les annotations correspondantes) que votre code est plus efficace qu'une simple boucle.
   Rappel : pour lancer les tests JMH, il suffit d'exécuter java -jar target/benchmarks.jar dans un terminal (et arrêter tout les programmes qui tournent !).


   <b>Réponse:</b>
   - intVector.
   - un VectorSpecies permet de dériver un type de vecteur et un nombre de lanes. On utilisera IntVector.SPECIES_128.
   - IntVector.SPECIES_PREFERRED.
   - length – length % SPECIES.length()
   - vecteur1.add(vecteur2)
   - v3.reduceLanes(VectorOperators.ADD);
   - for(; i < length; i++) { // post loop
     newArray[i] = array[i];
     }

      ```
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
      ```
     
   2. On souhaite écrire une méthode sumMask qui évite d'utiliser une post-loop et utilise un mask à la place.
      <br>- Comment peut-on faire une addition de deux vecteurs avec un mask ?
      <br>- Comment faire pour créer un mask qui allume les bits entre i la variable de boucle et length la longueur du tableau ?
      <br>Écrire le code de la méthode sumMask et vérifier que le test "testSumMask" passe.
      <br>Que pouvez vous dire en terme de performance entre sum et sumMask en utilisant les tests de performances JMH ?

      <b>Réponse:</b>
      - on transforme mask en vector correspondant puis on applique une opération lane-wise pour le calcul. Pour l'addition,
        on utilisera add().
      - ```var mask = SPECIES.indexInRange(i, length);```
      sumMask est plus rapide dans le cas où le nombre de valeurs n'est pas trop grand.
      ```
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
      ```
3. On souhaite maintenant écrire une méthode min qui calcule le minimum des valeurs d'un tableau en utilisant des vecteurs et une post-loop.
   Contrairement à la somme qui a 0 comme élément nul, le minimum n'a pas d'élément nul... Quelle doit être la valeur utilisée pour initialiser de toute les lanes du vecteur avant la boucle principale ?
   <br>Écrire le code de la méthode min, vérifier que le test nommé "testMin" passe et vérifier avec les tests JMH que votre code est plus efficace qu'une simple boucle sur les valeurs du tableau.
   
   <b>Réponse:</b>
   La valeur qui doit être utilisé pour initialiser le vecteur est la valeur maximal : Integer.MAX_VALUES.
   ```
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
   ```
   
4. On souhaite enfin écrire une méthode minMask qui au lieu d'utiliser une post-loop comme dans le code précédent, utilise un mask à la place.
   Attention, le minimum n'a pas d’élément nul (non, toujours pas !), donc on ne peut pas laisser des zéros "traîner" dans les llanes lorsque l'on fait un minimum sur deux vecteurs.
   <br>Écrire le code de la méthode minMask et vérifier que le test nommé "testMinMask" passe.
   <br>Que pouvez-vous dire en termes de performance entre min et minMask en utilisant les tests de performances JMH ?
   
   <b>Réponse:</b>
   ```
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
   ```
   minMask est plus rapide que min uniquement pour le 1er test.


## Exercice 3 - FizzBuzz
1. On souhaite écrire dans la classe FizzBuzz une méthode fizzBuzzVector128 qui prend en paramètre une longueur et renvoie un tableau d'entiers de taille longueur contenant les valeurs de FizzBuzz en utilisant des vecteurs 128 bits d'entiers.
   <bR>Écrire la méthode fizzBuzzVector128 sachant que les tableaux des valeurs et des deltas sont des constantes. Puis vérifier que votre implantation passe le test.
   <br>En exécutant les tests JMH, que pouvez-vous conclure en observant les différences de performance entre la version de base et la version utilisant l'API des vecteurs ?

<b>Réponses:</b>
```
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
```

2. On souhaite maintenant écrire une méthode fizzBuzzVector256 qui utilise des vecteurs 256 bits.
   <br>Une fois la méthode écrite, vérifier que celle-ci passe le test.
   <br>Utiliser les tests JMH pour vérifier la performance de votre implantation. Que pouvez vous en conclure en comparaison de la version utilisant des vecteurs 128 bits.

<b>Réponses:</b>
```
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
```