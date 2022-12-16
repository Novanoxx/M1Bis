### VONG Stéphane (groupe apprenti 2)
# TP11 Java 2022-2023
## Exercice 2 - NumericVec

1. Dans la classe fr.uge.numeric.NumericVec, on souhaite écrire une méthode longs sans paramètre qui permet de créer un NumericVec vide ayant pour l'instant une capacité fixe de 16 valeurs. Cela doit être la seule façon de pouvoir créer un NumericVec.
   <br>Écrire la méthode longs puis ajouter les méthodes add(value), get(index) et size.
   <br>Vérifier que les tests unitaires marqués "Q1" passent.

<b>Réponse :</b>
```
public class NumericVec<E> {
    private int size;
    private int capacity;
    private long[] interArray;
    
    private NumericVec() {
        this.size = 0;
        this.capacity = 16;
        this.interArray = new long[capacity];
    }
    
    public static NumericVec<Long> longs() {
        return new NumericVec<>();
    }
    
    public void add(E value) {
        Objects.requireNonNull(value);
        if (size == interArray.length) {
            this.capacity *= 2;
            interArray = Arrays.copyOf(interArray, capacity);
        }
        this.interArray[size()] = (long) value;
        this.size++;
    }

    public E get(int index) {
        Objects.checkIndex(index, size());
        return this.interArray[index];
    }

    public int size() {
        return size;
    }
}
```

2. On veut maintenant que le tableau utilisé par NumericVec puisse s'agrandir pour permettre d'ajouter un nombre arbitraire de valeurs.
   <br>On veut, de plus, que NumericVec soit économe en mémoire, donc la capacité du tableau d'un NumericVec vide doit être 0 (si vous n'y arrivez pas, faites la suite).
   <br>Vérifier que les tests unitaires marqués "Q2" passent.
   <br>Note: agrandir un tableau une case par une case est très inefficace !

<b>Réponse :</b>
```
public void add(E value) {
    Objects.requireNonNull(value);
    if (size == interArray.length) {
        this.capacity *= 2;
        interArray = Arrays.copyOf(interArray, capacity);
    }
    this.interArray[size()] = (long) value;
    this.size++;
}
```

3. Faire en sorte d'utiliser un Stream pour que l'on puisse afficher un NumericVec avec le format attendu.
   <br>Vérifier que les tests unitaires marqués "Q3" passent.

<b>Réponse:</b>
```
@Override
public String toString() {
    return Arrays.stream(interArray).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
}
```

4. On veut maintenant ajouter les 3 méthodes ints, longs et doubles qui permettent respectivement de créer des NumericVec d'int, de long ou de double en prenant en paramètre des valeurs séparées par des virgules.
   <br>En termes d'implantation, l'idée est de convertir les int ou les double en long avant de les insérer dans le tableau. Et dans l'autre sens, lorsque l'on veut lire une valeur, c'est à dire quand on prend un long dans le tableau, on le convertit en le type numérique attendu. Pour cela, l'idée est de stocker dans chaque NumericVec une fonction into qui sait convertir une valeur en long, et une fonction from qui sait convertir un long vers la valeur attendue.
   <br>Vérifier que les tests unitaires marqués "Q4" passent.

<b>Réponse :</b>
```
public class NumericVec<E> {
   private int size;
   private int capacity;
   private long[] interArray;

   private NumericVec() {
      this.size = 0;
      this.capacity = 16;
      this.interArray = new long[capacity];
   }
   
   public static NumericVec<Long> longs() {
      return new NumericVec<>();
   }
   
   public static NumericVec<Integer> ints() {
      return new NumericVec<>();
   }
   
   public static NumericVec<Double> doubles() {
      return new NumericVec<>();
   }
   
   public void add(E value) {
      Objects.requireNonNull(value);
      if (size == interArray.length) {
         this.capacity *= 2;
         interArray = Arrays.copyOf(interArray, capacity);
      }
      this.interArray[size()] = (long) value;
      this.size++;
   }
   
   public E get(int index) {
      Objects.checkIndex(index, size());
      return this.interArray[index];
   }
   
   public int size() {
      return size;
   }
   
   @Override
   public String toString() {
      return Arrays.stream(interArray).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
   }
}
```

5. On souhaite maintenant pouvoir parcourir un NumericVec avec une boucle for(:). Dans le cas où l'on modifie un NumericVec avec la méthode add lors de l'itération, les valeurs ajoutées ne sont pas visibles pour la boucle.
   <br>Modifier la classe NumericVec pour implanter le support de la boucle for(:).
   <br>Vérifier que les tests unitaires marqués "Q5" passent.

<b>Réponse :</b>
```
@Override
 public Iterator<E> iterator() {
     return new Iterator<>() {
         private int it = 0;
         private final int sizeIt = size;
         @Override
         public boolean hasNext() {
             return it < sizeIt;
         }

         @Override
         public E next() {
             if (!hasNext()) {
                 throw new NoSuchElementException("No next value");
             }
             var current = it;
             it++;
             return get(current);
         }
     };
 }
```

6. On souhaite ajouter une méthode addAll qui permet d'ajouter un NumericVec à un NumericVec déjà existant.
   <br>Écrire le code de la méthode addAll.
   <br>Vérifier que les tests unitaires marqués "Q6" passent.
   <br>Note: on peut remarquer qu'il y a une implantation efficace car les deux NumericVec utilisent en interne des tableaux de long.

<b>Réponse :</b>
```
public void addAll(NumericVec<? extends E> seq) {
   Objects.requireNonNull(seq);
   for (int i = 0; i < seq.size; i++) {
      add(seq.get(i));
   }
}
```

7. On souhaite maintenant écrire une méthode map(function, factory) qui prend en paramètre une fonction qui peut prendre en paramètre un élément du NumericVec et renvoie une nouvelle valeur ainsi qu' une référence de méthode permettant de créer un nouveau NumericVec qui contiendra les valeurs renvoyées par la fonction.
   <br>Écrire la méthode map.
   <br>Vérifier que les tests unitaires marqués "Q7" passent.

<b>Réponse :</b>
...

8. On souhaite écrire une méthode toNumericVec(factory) qui prend en paramètre une référence de méthode permettant de créer un NumericVec et renvoie un Collector qui peut être utilisé pour collecter des valeurs numériques dans un/des NumericVec créés par la référence de méthode.
   <br>Écrire la méthode toNumericVec.
   <br>Vérifier que les tests unitaires marqués "Q8" passent.

<b>Réponse :</b>
...

9. Enfin, écrire une méthode stream() qui renvoie un Stream qui voit l'ensemble des valeurs par ordre d'insertion dans le NumericVec courant.
   <br>Le Stream renvoyé devra être parallélisable.
   <br>Vérifier que les tests unitaires marqués "Q9" passent.
   <br>Note : pour que le Stream parallèle soit un peu efficace, on ne coupera pas en deux les Stream ayant moins de 1024 valeurs.

<b>Réponse :</b>
...