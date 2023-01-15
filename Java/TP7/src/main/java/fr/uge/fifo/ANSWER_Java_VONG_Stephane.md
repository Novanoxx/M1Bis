### VONG Stéphane (groupe apprenti 2)
# TP7 Java 2022-2023
## Exercice 2 - Fifo

1. Cette représentation peut poser problème, car si la tête et la queue correspondent au même indice, il n'est pas facile de détecter si cela veux dire que la file est pleine ou vide.
   <br>Comment doit-on faire pour détecter si la file est pleine ou vide ?

<b>Réponse:</b>
On peut utiliser un compteur pour compter le nombre de valeurs dans la file d'attente et le comparer a un champs qui donne le nombre d'élément dans la file.

2. Écrire une classe Fifo générique (avec une variable de type E) dans le package fr.uge.fifo prenant en paramètre le nombre maximal d’éléments que peut stocker la structure de données. Pensez à vérifier les préconditions.

<b>Réponse:</b>
```
public class Fifo<E> {
    private int head;
    private int tail;
    private int count;
    private final int capacity;
    private E[] queue;
    @SuppressWarnings("unchecked")
    public Fifo(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must greater than 0");
        }
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
        this.count = 0;
        this.queue = (E[]) new Object[capacity];
    }
}
```

3. Écrire la méthode offer qui ajoute un élément de type E dans la file. Pensez à vérifier les préconditions sachant que, notamment, on veut interdire le stockage de null.
   <br>Comment détecter que la file est pleine ?
   <br>Que faire si la file est pleine ?

<b>Réponse:</b>
Pour détecter si la file est pleine, on va utiliser le compteur créée exprès.
Si la file est pleine, on va renvoyer un IllegalStateException.
```
public void offer(E value) {
   Objects.requireNonNull(value);
   if (count == capacity) {
      throw new IllegalStateException("queue is full");
   }
   if (tail == capacity) {
      tail = 0;
   }
   queue[tail] = value;
   tail++;
   count++;
}
```

4. Écrire une méthode poll qui retire un élément de type E de la file. Penser à vérifier les préconditions.
   <br>Que faire si la file est vide ? 

<b>Réponse:</b>
Si la liste est vide, on va throw un IllegalStateException.
```
public E poll() {
   if (count == 0) {
      throw new IllegalStateException("queue is empty");
   }
   if (head == capacity) {
      head = 0;
   }
   var res = queue[head];
   head++;
   count--;
   return res;
}
```

5. Ajouter une méthode d'affichage qui affiche les éléments dans l'ordre dans lequel ils seraient sortis en utilisant poll. L'ensemble des éléments devra être affiché entre crochets ('[' et ']') avec les éléments séparés par des virgules (suivies d'un espace).

<b>Réponse:</b>
```
@Override
public String toString() {
   StringJoiner joiner = new StringJoiner(", ", "[", "]");
   if (count == 0) {
      return joiner.toString();
   }
   int i = head;
   int counter = 0;
   while (i != tail || counter < count) {
      if (i == capacity) {
         i = 0;
      }
   joiner.add(queue[i].toString());
   i++;
   counter++;
   }
   return joiner.toString();
}
```

6. Rappelez ce qu'est un memory leak en Java et assurez-vous que votre implantation n'a pas ce comportement indésirable.

<b>Réponse:</b>
Un memory leak est une valeur initialisée mais jamais utilisée.

7. Ajouter une méthode size et une méthode isEmpty.

<b>Réponse:</b>
```
public int size() {
   return count;
}

public boolean isEmpty() {
   return count == 0;
}
```

8. Rappelez quel est le principe d'un itérateur.
<br>Quel doit être le type de retour de la méthode iterator() ?

<b>Réponse:</b>
Un itérateur est comme une boucle for sauf qu'on a le contrôle sur le "pointeur" (l'index choisi). On souhaiterait effectuer une opération sur chaque élément.
iterator() envoie un Iterator\<E>

9. Implanter la méthode iterator().
   <br>Note : ici, pour simplifier le problème, on considérera que l'itérateur ne peut pas supprimer des éléments pendant son parcours.

<b>Réponse:</b>
```
public Iterator<E> iterator() {
   return new Iterator<>() {
      int it_head = head;
      int it_count = count;
      @Override
      public boolean hasNext() {
          return it_count >= 1;
      }
      
      @Override
      public E next() {
          if (it_count == 0) {
              throw new NoSuchElementException("No next element");
          }
          var elem = queue[it_head];
          it_head = (it_head + 1) % queue.length;
          it_count -= 1;
          return elem;
      }
};
```

10. Rappeler à quoi sert l'interface Iterable.
    <br>Faire en sorte que votre file soit Iterable.

<b>Réponse:</b>
On impélemente juste Iterable\<E>

## Exercice 3 - ResizeableFifo
1. Indiquer comment agrandir la file si celle-ci est pleine et que l'on veut doubler sa taille. Attention, il faut penser au cas où le début de la liste a un indice qui est supérieur à l'indice indiquant la fin de la file.
   <br>Implanter la solution retenue dans une nouvelle classe ResizeableFifo.
   <br>Note: il existe les méthodes Arrays.copyOf et System.arraycopy.

<b>Réponse:</b>
On ajoute la méthode doubleSize() et on modifie les méthodes offer().
```
private void doubleSize() {
   capacity *= 2;
   E[] copy = (E[]) new Object[capacity];
   System.arraycopy(queue, head, copy, 0, count - head);
   System.arraycopy(queue, 0, copy, tail, head);
   queue = copy;
   head = 0;
   tail = count;
}
```


2. En fait, il existe déjà une interface pour les files dans le JDK appelée java.util.Queue.
   Sachant qu'il existe une classe AbstractQueue qui fournit déjà des implantations par défaut de l'interface Queue indiquer:
   - quelles sont les méthodes supplémentaires à implanter;
   - quelles sont les méthodes dont l'implantation doit être modifiée;
   - quelles sont les méthodes que l'on peut supprimer.

<b>Réponse:</b>
La méthode supplémentaire a implanter est peek() d'AbstractQueue.
La méthode dont l'implantation doit être modifiée est poll() car elle renvoie null au lieu de sortir un IllegalStateException.
On peut en soit supprimer la méthode iterator puisqu'on a implémenté Iterable() qui a sa propre méthode iterator. 
