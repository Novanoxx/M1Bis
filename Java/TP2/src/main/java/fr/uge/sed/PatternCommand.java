package fr.uge.sed;

import java.util.Objects;
import java.util.regex.Pattern;

public record PatternCommand(Pattern pattern) {
    public PatternCommand {
        Objects.requireNonNull(pattern);
    }
}
