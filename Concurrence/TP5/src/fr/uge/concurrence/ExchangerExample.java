package fr.uge.concurrence;

import java.util.concurrent.Exchanger;

public class ExchangerExample {
  public static void main(String[] args) throws InterruptedException {
    var exchanger = new Exchanger<String>();
    Thread.ofPlatform().start(() -> {
      try {
        System.out.println("thread 1 " + exchanger.exchange("foo1"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println("main " + exchanger.exchange(null));
  }
}

/*
Quel est l'affichage attendu ?
thread 1 null      ou    main foo1
main foo1                thread 1 null

Comment faire pour distinguer le premier et le second appel à la méthode exchange ?
On regarde si la queue de Exchanger est vide ou non, si elle est vide c'est le 1er appel, sinon c'est le 2eme.
 */