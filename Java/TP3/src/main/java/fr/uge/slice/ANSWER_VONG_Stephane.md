### VONG Stéphane (groupe apprenti 2)
# TP3 Java 2022-2023
## Exercice 2 - The Slice and The furious

1. Implanter la classe Slice et les méthodes array, size et get(index).
   <br>Vérifier que les tests JUnit marqués "Q1" passent.

<b>Réponse:</b>
```
sealed interface Slice<E> permits Slice.ArraySlice {
    static <E> Slice<E> array(E[] array) {
        Objects.requireNonNull(array);
        return new ArraySlice<>(array);
    }
    int size();
    E get(int index);

   final class ArraySlice<T> implements Slice<T> {
        private final T[] lst;

        private ArraySlice(T[] array) {
            this.lst = array;
        }
       @Override
       public int size() {
           return lst.length;
       }

       @Override
       public T get(int index) {
           return lst[index];
       }
   }
}
```
2. On souhaite que l'affichage d'un slice affiche les valeurs séparées par des virgules avec un '[' et un ']' comme préfixe et suffixe.
   Par exemple,
    ```
   var array = new String[] { "foo", "bar" };
   var slice = Slice.array(array);
   System.out.println(slice);   // [foo, bar]
   ```
    En terme d'implantation, penser à utiliser un Stream avec le bon Collector !
<br>Vérifier que les tests JUnit marqués "Q2" passent.

<b>Réponse:</b>
```
@Override
public String toString() {
    return Arrays.toString(Arrays.stream(lst).toArray());
}
```

3. On souhaite ajouter une surcharge à la méthode array qui, en plus de prendre le tableau en paramètre, prend deux indices from et to et montre les éléments du tableau entre from inclus et to exclus.
   Par exemple
    ```
   String[] array = new String[] { "foo", "bar", "baz", "whizz" };
    Slice<String> slice = Slice.array(array, 1, 3);
   ```
    En terme d'implantation, on va créer une autre classe interne nommée SubArraySlice implantant l'interface Slice.
<br>Vérifier que les tests JUnit marqués "Q3" passent.
<br>Note : il existe une méthode Arrays.stream(array, from, to) dans la classe java.util.Arrays

<b>Réponse:</b>
```
final class SubArraySlice<U> implements Slice<U> {
    private final U[] lst;
    private final int from;
    private final int to;
    private SubArraySlice(U[] array, int from, int to) {
        Objects.requireNonNull(array);
        if (from < 0 || to < 0 || (from > to) || to - from > array.length) {
            throw new IndexOutOfBoundsException();
        }
        this.lst = array;
        this.from = from;
        this.to = to;
    }
    @Override
    public int size() {
        return to - from;
    }

    @Override
    public U get(int index) {
        Objects.checkIndex(index, to - from) ;
        return lst[from + index];
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.stream(lst, from, to).toArray());
    }
}
```

4. On souhaite enfin ajouter une méthode subSlice(from, to) à l'interface Slice qui renvoie un sous-slice restreint aux valeurs entre from inclus et to exclu.
   Par exemple,
   ```
   String[] array = new String[] { "foo", "bar", "baz", "whizz" };
   Slice<String> slice = Slice.array(array);
   Slice<String> slice2 = slice.subSlice(1, 3);
   ```
   Bien sûr, cela veut dire implanter la méthode subSlice(from, to) dans les classes ArraySlice et SubArraySlice.
<br>Vérifier que les tests JUnit marqués "Q4" passent.
```
@Override
public Slice<U> subSlice(int from, int to) {
   if (from < 0 || to < 0 || from > to || from > lst.length || to > lst.length || size() < to - from) {
      throw new IndexOutOfBoundsException();
   }
  return new SubArraySlice<>(lst, from + this.from, to + this.from);
}
```

## Exercice 3 - 2 Slice 2 Furious
4. Dans quel cas va-t-on utiliser une inner class plutôt qu'une classe interne ? 
<b>Réponse:</b> On va utiliser une inner class dans le cas où la classe B dans la classe A n'est pas utilisé ailleurs que dans
la classe A.

## Exercice 4 - The Slice and The Furious: Tokyo Drift
3. Dans quel cas va-t-on utiliser une classe anonyme plutôt qu'une classe interne ?
<b>Réponse:</b> On va utiliser une classe anonyme dans le cas où on veut seulement redéfinir un comportement
d'une classe ou interface déjà existante.