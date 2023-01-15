package fr.uge.concurrence;

/*
Expliquer pourquoi la classe HonorBoard n'est pas thread-safe.
HonorBoard n'est pas thread-safe car toString effectue plusieurs opération lors de son lancement,
ce qui peut donner la possibilité d'avoir "Mickey Duck" en résultat.

Maintenant que votre classe est thread-safe, peut-on remplacer ces lignes ?
Oui, tant qu'on utilise les block synchronized entre les return.
 */
public class HonorBoard {
  private String firstName;
  private String lastName;
  private final Object lock = new Object();
  
  public void set(String firstName, String lastName) {
    synchronized (lock) {
      this.firstName = firstName;
      this.lastName = lastName;
    }
  }
  
  @Override
  public String toString() {
    synchronized (lock) {
      return firstName + ' ' + lastName;
    }
  }

  public String firstName() {
    synchronized (lock) {
      return firstName;
    }
  }

  public String lastName() {
    synchronized (lock) {
      return lastName;
    }
  }
  
  public static void main(String[] args) {
    var board = new HonorBoard();
    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Mickey", "Mouse");
      }
    });
    
    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Donald", "Duck");
      }
    });
    
    Thread.ofPlatform().start(() -> {
      for(;;) {
        System.out.println(board);
      }
    });
  }
}
