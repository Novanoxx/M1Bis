package fr.uge.sed;

public record LineDeleteCommand(int line) {
    public LineDeleteCommand {
        if (line < 0) {
            throw new IllegalArgumentException();
        }
    }
}
