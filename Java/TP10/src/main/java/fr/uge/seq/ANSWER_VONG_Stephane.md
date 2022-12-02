### VONG Stéphane (groupe apprenti 2)
# TP10 Java 2022-2023
## Exercice 2 - Seq

1. Écrire le code de la classe Seq dans le package fr.uge.seq.

<b>Réponse :</b>
```
public class Seq<E> {
    private final List<?> lst;

    public Seq(List<? extends E> lst) {
        this.lst = lst;
    }

    public static <E> Seq<E> from(List<? extends E> lst) {
        Objects.requireNonNull(lst);
        return new Seq<>(List.copyOf(lst));
    }

    public Object get(int index) {
        if (index < 0 || index > lst.size()) {
            throw new IndexOutOfBoundsException("index is not in lst");
        }
        return lst.get(index);
    }

    public int size() {
        return lst.size();
    }
}
```

2. Écrire une méthode d'affichage permettant d'afficher les valeurs d'un Seq séparées par des virgules (suivies d'un espace), <br>l'ensemble des valeurs étant encadré par des chevrons ('<' et '>').
   <br>Par exemple, avec le Seq créé précédemment
```
System.out.println(seq);  // <78, 56, 34, 23>
```

<b>Réponse: </br>
```
@Override
 public String toString() {
     StringJoiner joiner = new StringJoiner(", ", "<", ">");
     for (var elem : lst) {
         joiner.add(elem.toString());
     }
     return joiner.toString();
 }
```

3. Écrire une méthode of permettant d'initialiser un Seq à partir de valeurs séparées par des virgules.
   <br>Par exemple, on pourra créer le Seq précédent comme ceci
```
var seq = Seq.of(78, 56, 34, 23);
```

<b>Réponse :</b>
```
public void forEach(Consumer<? super E> consumer) {
   Objects.requireNonNull(consumer);
   for (var elem : lst) {
      consumer.accept((E) elem);
   }
}
```

5. On souhaite écrire une méthode map qui prend en paramètre une fonction à appliquer à chaque élément d'un Seq pour créer un nouveau Seq. On souhaite avoir une implantation paresseuse, c'est-à-dire une implantation qui ne fait pas de calcul si ce n'est pas nécessaire. Par exemple, tant que personne n'accède à un élément du nouveau Seq, il n'est pas nécessaire d'appliquer la fonction. L'idée est de stoker les anciens éléments ainsi que la fonction et de l'appliquer seulement si c'est nécessaire.
   <br>Bien sûr, cela va nous obliger à changer l'implantation déjà existante de Seq car maintenant tous les Seq vont stocker une liste d'éléments ainsi qu'une fonction de transformation (de mapping).
   <br>Exemple d'utilisation 
```
var seq2 = seq.map(String::valueOf); // String.valueOf() est pas appelée
System.out.println(seq2.get(0));     // "78", String.valueOf a été appelée 1 fois
                                     // car on demande explicitement la valeur
```
Avant de se lancer dans l'implantation de map, quelle doit être sa signature ?
<br>Quel doit être le type des éléments de la liste ? Et le type de la fonction stockée ?
<br>Faire les modifications correspondantes, puis changer le code des méthodes pour les prendre en compte. Enfin, écrire le code de map. 

<b>Réponse :</b>
```
 private final List<?> lst;
 private final Function<Object, E> mapper;

 public Seq(List<? extends E> lst) {
     Objects.requireNonNull(lst);
     this.lst = List.copyOf(lst);
     mapper = e -> (E) e;
 }

 public Seq(List<?> lst, Function<Object, E> mapper) {
     Objects.requireNonNull(lst);
     Objects.requireNonNull(mapper);
     this.lst = List.copyOf(lst);
     this.mapper = mapper;
 }

 public static <E> Seq<E> from(List<? extends E> lst) {
     Objects.requireNonNull(lst);
     return new Seq<>(List.copyOf(lst));
 }

 @SafeVarargs
 public static <E> Seq<E> of(E... value) {
     Objects.requireNonNull(value);
     return new Seq<>(List.of(value));
 }

 public <F> Seq<F> map(Function<? super E, ? extends F> func) {
     Objects.requireNonNull(func);
     return new Seq<>(lst, this.mapper.andThen(func));
 }

 public Object get(int index) {
     if (index < 0 || index > lst.size()) {
         throw new IndexOutOfBoundsException("index is not in lst");
     }
     return mapper.apply(lst.get(index));
 }

 public int size() {
     return lst.size();
 }

 @Override
 public String toString() {
     StringJoiner joiner = new StringJoiner(", ", "<", ">");
     for (var elem : lst) {
         joiner.add(mapper.apply(elem).toString());
     }
     return joiner.toString();
 }

 public void forEach(Consumer<? super E> consumer) {
     Objects.requireNonNull(consumer);
     for (var elem : lst) {
         consumer.accept(mapper.apply(elem));
     }
 }
```

6. Écrire une méthode findFirst qui renvoie le premier élément du Seq si celui-ci existe.

<b>Réponse :</b>
```
public Optional<E> findFirst() {
   if (size() <= 0) {
      return Optional.empty();
   }
   return Optional.of(mapper.apply(lst.get(0)));
}
```

7. Faire en sorte que l'on puisse utiliser la boucle for-each-in sur un Seq
   Par exemple,
```
for(var value : seq) {
   System.out.println(value);
}
```

<b>Réponse: </b>
```

```

8. 