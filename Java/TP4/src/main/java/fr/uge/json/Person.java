package fr.uge.json;

import static java.util.Objects.requireNonNull;

public record Person(@JSONProperty("firstName") String firstName, @JSONProperty("lastName") String lastName) {
    public Person {
        requireNonNull(firstName);
        requireNonNull(lastName);
    }
}
