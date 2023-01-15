### VONG Stéphane (groupe apprenti 2)
# TP8 Java 2022-2023
## Exercice 2 - TimeSeries

1. Dans un premier temps, on va créer une classe TimeSeries ainsi qu'un record Data à l'intérieur de la classe TimeSeries qui représente une paire contenant une valeur de temps (timestamp) et un élément (element).
<br>Le record Data est paramétré par le type de l'élément qu'il contient.
<br>Écrire la classe TimeSeries dans le package fr.uge.serie, ainsi que le record interne public Data et vérifier que les tests marqués "Q1" passent.

<b>Réponse: </b>
```
public class TimeSeries<T> {
    private final ArrayList<Data<T>> timeseries = new ArrayList<>();
    private long timeOrder = Long.MIN_VALUE;
    
    record Data<T>(long timestamp, T element) {
        public Data {
            Objects.requireNonNull(element);
        }
    }
}
```

2. On souhaite maintenant écrire les méthodes dans TimeSeries :
   <br>add(timestamp, element) qui permet d'ajouter un élément avec son timestamp.
   <br>La valeur de timestamp doit toujours être supérieure ou égale à la valeur du timestamp précédemment inséré (s'il existe).
   size qui renvoie le nombre d'éléments ajoutés.
   <br>get(index) qui renvoie l'objet Data se trouvant à la position indiquée par l'index (de 0 à size - 1).
<br>En interne, la classe TimeSeries stocke des instances de Data dans une liste qui s'agrandit dynamiquement.
<br>Écrire les 3 méthodes définies ci-dessus et vérifier que les tests marqués "Q2" passent.

<b>Réponse: </b>
```
public void add(long timestamp, T elem) {
    Objects.requireNonNull(elem);
    if (timeOrder > timestamp) {
        throw new IllegalStateException("timestamp not big enough");
    }
    timeseries.add(new Data<>(timestamp, elem));
    timeOrder = timestamp;
}

public int size() {
    return timeseries.size();
}

public Data get(int index) {
    return timeseries.get(index);
}
```

3. On souhaite maintenant créer une classe interne publique Index ainsi qu'une méthode index permettant de créer un Index stockant les indices des données de la TimeSeries sur laquelle la méthode index est appelée. L'objectif est de pouvoir ensuite accéder aux Data correspondantes dans le TimeSeries. Un Index possède une méthode size indiquant combien d'indices il contient.
   <br>Seuls les indices des éléments ajoutés avant l'appel à la méthode index() doivent être présents dans l'Index.
   <br>En interne, un Index stocke un tableau d'entiers correspondants à chaque indice.
   <br>Écrire la méthode index et vérifier que les tests marqués "Q3" passent.

<b>Réponse :</b>
```
class Index {
  private final int[] indexList;

  private Index(int[] lst) {
      this.indexList = Arrays.copyOf(lst, lst.length);
  }
}
```

4.  On souhaite pouvoir afficher un Index, c'est à dire afficher les éléments (avec le timestamp) référencés par un Index, un par ligne avec un pipe (|) entre le timestamp et l'élément.
    <br>Faites les changements qui s'imposent dans la classe Index et vérifier que les tests marqués "Q4" passent.

<b>Réponse :</b>
```
public String toString() {
   StringBuilder builder = new StringBuilder();
   if (size() < 1) {
       return "";
   }
   for (int i : indexList) {
       builder.append(timeseries.get(i).timestamp)
               .append(" | ")
               .append(timeseries.get(i).element)
               .append("\n");

   }
   builder.deleteCharAt(builder.length() - 1);
   return builder.toString();
}
```

5. On souhaite ajouter une autre méthode index(lambda) qui prend en paramètre une fonction/lambda qui est appelée sur chaque élément de la TimeSeries et indique si l'élément doit ou non faire partie de l'index.
   <br>Par exemple, avec une TimeSeries contenant les éléments "hello", "time" et "series" et une lambda s -> s.charAt(1) == 'e' qui renvoie vrai si le deuxième caractère est un 'e', l'Index renvoyé contient [0, 2].
   <br>Quel doit être le type du paramètre de la méthode index(lambda) ?
   <br>Écrire la méthode index(filter) et vérifier que les tests marqués "Q5" passent.
   <br>Note : On peut remarquer qu'il est possible de ré-écrire la méthode index sans paramètre pour utiliser celle avec un paramètre. 

<b>Réponse :</b>
```
public Index index(Predicate<? super T> filter) {
     return new Index(filter, size());
 }
```

6. Dans la classe Index, écrire une méthode forEach(lambda) qui prend en paramètre une fonction/lambda qui est appelée avec chaque Data référencée par les indices de l'Index.
   <br>Par exemple, avec la TimeSeries contenant les Data (24 | "hello"), (34 | "time") et (70 | "series") et un Index [0, 2], la fonction sera appelée avec les Data (24 | "hello") et (70 | "series").
   <br>Quel doit être le type du paramètre de la méthode forEach(lambda) ?
   <br>Écrire la méthode forEach(lambda) dans la classe Index et vérifier que les tests marqués "Q6" passent.

<b>Réponse :</b>
```
public void forEach(Consumer<? super Data<T>> function) {
   Arrays.stream(indexList).forEach(e -> function.accept(timeseries.get(e)));
}
```

7. Quelle interface doit implanter la classe Index pour pouvoir être utilisée dans une telle boucle ?
   <br>Quelle méthode de l'interface doit-on implanter ? Et quel est le type de retour de cette méthode ? Faites les modifications qui s'imposent dans la classe Index et vérifiez que les tests marqués "Q7" passent.

<b>Réponse :</b>
Nous devons implémenter Iterable dans Index pour utiliser Index dans des boucle forEach.
Nous devons implémenter les méthodes de iterator(). Elle devra renvoyer un Iterator.

```
public Iterator<Data<T>> iterator() {
   return new Iterator<Data<T>>() {
       private int i;

       @Override
       public boolean hasNext() {
           return size() != i;
       }

       @Override
       public Data<T> next() {
           if (!hasNext()) {
               throw new NoSuchElementException("Have not next");
           }
           return timeseries.get(indexList[i++]);
       }
   };
}
```

8. On veut ajouter une méthode or sur un Index qui prend en paramètre un Index et renvoie un nouvel Index qui contient à la fois les indices de l'Index courant et les indices de l'Index passé en paramètre.
   <br>Il ne doit pas être possible de faire un or avec deux Index issus de TimeSeries différentes.
   <br>En termes d'implantation, on peut faire une implantation en O(n) mais elle est un peu compliquée à écrire. On se propose d'écrire une version en O(n.log(n)) en concaténant les Stream de chaque index puis en triant les indices et en retirant les doublons.
   <br>Expliquer pourquoi on ne peut pas juste concaténer les deux tableaux d'indices ?
   <br>Écrire le code de la méthode or(index) dans la classe Index et vérifier que les tests marqués "Q8" passent. 

<b>Réponse :</b>
On ne peut pas concaténer 2 tableaux d'index car ça ne sera plus trier.
Dans Index:
```
public Index or(Index index) {
   if (!timeSeries.equals(index.timeSeries)) {
       throw new IllegalArgumentException("It is not the same TimeSeries");
   }
   return new Index(IntStream.concat(Arrays.stream(indexList), Arrays.stream(index.indexList))
           .sorted()
           .distinct()
           .toArray());
}
```
Dans TimeSeries:
```
@Override
 public boolean equals(Object o) {
     if (this == o) {
         return true;
     }
     if (o instanceof TimeSeries<?>) {
         return false;
     }
     TimeSeries<?> that = (TimeSeries<?>) o;
     return Objects.equals(timeseries, that.timeseries);
 }

 @Override
 public int hashCode() {
     return Objects.hash(timeseries);
 }
```

9. Même question que précédemment, mais au lieu de vouloir faire un or, on souhaite faire un and entre deux Index.
   <br>En termes d'implantation, il existe un algorithme en O(n) qui est en O(1) en mémoire. À la place, nous allons utiliser un algorithme en O(n) mais qui utilise O(n) en mémoire. L'idée est de prendre un des tableaux d'indices et de stocker tous les indices dans un ensemble sans doublons puis de parcourir l'autre tableau d'indices et de vérifier que chaque indice est bien dans l'ensemble.
   <br>Écrire le code de la méthode and(index) dans la classe Index et vérifier que les tests marqués "Q9" passent.

<b>Réponse :</b>
```
public Index and(Index index) {
   Objects.requireNonNull(index);
   if (!timeSeries.equals(index.timeSeries)) {
       throw new IllegalArgumentException("It is not the same TimeSeries");
   }
   var tmp = Arrays.stream(index.indexList).boxed().collect(Collectors.toSet());
   var res = Arrays.stream(indexList).filter(tmp::contains).toArray();
   return new Index(res);
}
```