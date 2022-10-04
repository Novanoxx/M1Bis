package fr.uge.sed;

import java.util.Objects;
import java.util.regex.Pattern;

public record PatternCommand(Pattern pattern) implements Command{
    public PatternCommand {
        Objects.requireNonNull(pattern);
    }
}
