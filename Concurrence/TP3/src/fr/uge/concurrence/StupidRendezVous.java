package fr.uge.concurrence;

import java.util.Objects;

/**
 * Note: this code does several stupid things !
 * Que se passe-t-il lorsqu'on exécute ce code ?
 * On ne sait pas quel thread a trouvé le nombre premier.
 *
 * Commenter l'instruction Thread.sleep(1) dans la méthode get puis ré-exécuter le code.
 * Que se passe-t-il ? Expliquer où est le bug ?
 * Le programme ne se termine pas apres avoir trouvé un nombre premier. Le bug se trouve dans la boucle while de la
 * méthode get(). L'optimisation JIT empeche la vérification de la condition du while.
 */
public class StupidRendezVous<V> {
  private V value;
  
  public void set(V value) {
    Objects.requireNonNull(value);
    this.value = value;
  }
  
  public V get() throws InterruptedException {
    while(value == null) {
        //Thread.sleep(1);  // then comment this line !
    }
    return value;
  }
  
  public static void main(String[] args) throws InterruptedException {
    StupidRendezVous<String> rendezVous = new StupidRendezVous<>();
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
      rendezVous.set("hello");
    });
    
    System.out.println(rendezVous.get());
  }
}
