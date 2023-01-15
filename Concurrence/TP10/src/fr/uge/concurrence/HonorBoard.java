package fr.uge.concurrence;

public class HonorBoard {
    private volatile Person person = new Person(null, null);
    private record Person(String firstName, String lastName) {}
    public void set(String firstName, String lastName) {
        this.person = new Person(firstName, lastName);      // volatile write
    }

    @Override
    public String toString() {
        var person = this.person;                   // volatile read
        return person.firstName + ' ' + person.lastName;
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

/**
 * - Est-il toujours possible de voir des affichages de Mickey Duck ou Donald Mouse ?
 *   Oui
 */
