### VONG Stéphane (groupe apprenti 2)
# TP9 Java 2022-2023
## Exercice 2 - MatrixGraph

1. Indiquer comment trouver la case (i, j) dans un tableau à une seule dimension de taille nodeCount * nodeCount.

<b>Réponse :</b>
i * nodeCount + j

2. Rappeler pourquoi, en Java, il n'est pas possible de créer des tableaux de variables de type puis implanter la classe MatrixGraph et son constructeur.
   <br>Pouvez-vous supprimer le warning à la construction ? Pourquoi?
   <br>Vérifier que les tests marqués "Q2" passent.

<b>Réponse :</b>
On peut supprimer le warning avec un SuppressWarning("unchecked").
```
public class MatrixGraph<E> implements Graph<E> {
    private E[] array;
    
    @SuppressWarnings("unchecked")
    public MatrixGraph(int nodeCount) {
        this.array = (E[]) new Object[nodeCount * nodeCount];
    }
}
```

3. On peut remarquer que la classe MatrixGraph n'apporte pas de nouvelles méthodes par rapport aux méthodes de l'interface Graph donc il n'est pas nécessaire que la classe MatrixGraph soit publique.
   <br>Ajouter une méthode factory nommée createMatrixGraph dans l'interface Graph et déclarer la classe MatrixGraph non publique.
   <br>Vérifier que les tests marqués "Q3" passent.

<b>Réponse :</b>
```
static <T> Graph<T> createMatrixGraph(int nodeCount) {
   return new MatrixGraph<>(nodeCount);
}
```

4. Afin d'implanter correctement la méthode getWeight, rappeler à quoi sert la classe java.util.Optional en Java.
   <br>Implanter la méthode addEdge sachant que l'on ne peut pas créer un arc sans valeur.
   <br>Implanter la méthode getWeight.
   <br>Vérifier que les tests marqués "Q4" passent.

<b>Réponse :</b>
```
@Override
 public void addEdge(int src, int dst, E weight) {
     Objects.checkIndex(src, nodeCount);
     Objects.checkIndex(dst, nodeCount);
     Objects.checkIndex(offset(src, dst), nodeCount * nodeCount);

     array[offset(src, dst)] = weight;
 }

 @Override
 public Optional<E> getWeight(int src, int dst) {
     Objects.checkIndex(src, nodeCount);
     Objects.checkIndex(dst, nodeCount);
     return array[offset(src, dst)];
 }
```

5. Implanter la méthode edges puis vérifier que les tests marqués "Q5" passent.

<b>Réponse :</b>
```
@Override
public void edges(int src, EdgeConsumer<? super E> edgeConsumer) {
   Objects.requireNonNull(edgeConsumer);
   Objects.checkIndex(src, nodeCount);
   IntStream.range(0, nodeCount).forEach(i -> getWeight(src, i).ifPresent(v -> edgeConsumer.edge(src, i, v)));
}
```

6. Rappeler le fonctionnement d'un itérateur et de ses méthodes hasNext et next.
   <br>Que renvoie next si hasNext retourne false ?
   <br>Expliquer pourquoi il n'est pas nécessaire, dans un premier temps, d'implanter la méthode remove qui fait pourtant partie de l'interface.
   <br>Implanter la méthode neighborsIterator(src) qui renvoie un itérateur sur tous les nœuds ayant un arc dont la source est src.
   <br>Vérifier que les tests marqués "Q6" passent.

<b>Réponse :</b>
Un itérateur est un objet qui permet de parcourir une structure de données, hasNext() permet de savoir si il y a un suivant
et next() donne la valeur. remove() ne sert a rien ici car on peut remplacer les valeurs.
```
@Override
public Iterator<Integer> neighborIterator(int src) {
   Objects.checkIndex(src, nodeCount);
   return new Iterator<Integer>() {
      private int it = checkIndex(0);
      
      @Override
      public boolean hasNext() {
         return it != nodeCount;
      }
      
      private int checkIndex(int index) {
         for (int i = index; i < nodeCount; i++) {
            if (getWeight(src, i).isPresent()) {
               return i;
            }
         }
         return nodeCount;
      }
      
      @Override
      public Integer next() {
         if (!hasNext()) {
            throw new NoSuchElementException("There is no more element");
         }
         var current = it;
         it = checkIndex(it + 1);
         return current;
      }
   };
}   
```

7. Pourquoi le champ nodeCount ne doit pas être déclaré private avant Java 11 ?
   <br>Est-ce qu'il y a d'autres champs qui ne doivent pas être déclarés private avant Java 11 ?

8. On souhaite écrire la méthode neighborStream(src) qui renvoie un IntStream contenant tous les nœuds ayant un arc sortant par src.
   <br>Pour créer le Stream ,nous allons utiliser StreamSupport.intStream qui prend en paramètre un Spliterator.OfInt. Rappeler ce qu'est un Spliterator, à quoi sert le OfInt et quelles sont les méthodes qu'il va falloir redéfinir.
   <br>Écrire la méthode neighborStream sachant que l'on implantera le Spliterator en utilisant l'itérateur défini précédemment.
   <br>Vérifier que les tests marqués "Q8" passent.

<b>Réponse :</b>
Un Spliterator est un objet qui compose un Stream de manière interne. C'est un iterator qui va séparer la structure de donnée
en groupe de valeurs et utiliser plusieurs itérateurs qui vont parcourir chaque groupe de valeurs.
```
@Override
public IntStream neighborStream(int src) {
   Objects.checkIndex(src, nodeCount);
   var it = neighborIterator(src);
   return StreamSupport.intStream(new Spliterator.OfInt() {
      @Override
      public OfInt trySplit() {
         return null;
      }
   
      @Override
      public long estimateSize() {
         return Long.MAX_VALUE;
      }
   
      @Override
      public int characteristics() {
         return 0;
      }
   
      @Override
      public boolean tryAdvance(IntConsumer action) {
         if (it.hasNext()) {
            action.accept(it.next());
            return true;
         }
         return false;
      }
   }, false);
}
```

9. On peut remarquer que neighborStream dépend de neighborsIterator et donc pas d'une implantation spécifique. On peut donc écrire neighborStream directement dans l'interface Graph comme ça le code sera partagé.
   <br>Rappeler comment on fait pour avoir une méthode 'instance avec du code dans une interface.
   <br>Déplacer neighborStream dans Graph et vérifier que les tests unitaires passent toujours.

<b>Réponse :</b>
On fait un default pour avoir une méthode dans une interface.

10. Expliquer le fonctionnement précis de la méthode remove de l'interface Iterator.
    <br>Implanter la méthode remove de l'itérateur.
    <br>Vérifier que les tests marqués "Q10" passent.

<b>Réponse :</b>
...

11. On peut remarquer que l'on peut ré-écrire edges en utilisant neighborsStream, en une ligne :) et donc déplacer edges dans Graph.
    <br>Déplacer le code de la méthode edges dans Graph.
