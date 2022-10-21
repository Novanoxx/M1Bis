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
2. 