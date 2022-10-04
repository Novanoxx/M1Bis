package fr.uge.sed;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamEditor {
    private final LineDeleteCommand cmdDelete;
    private final PatternCommand cmdPattern;
    private enum Action {
        DELETE, PRINT
    }
    public StreamEditor(LineDeleteCommand cmdDelete) {
        this.cmdPattern = new PatternCommand(Pattern.compile(" "));
        this.cmdDelete = cmdDelete;
    }

    public StreamEditor(PatternCommand cmdPattern) {
        this.cmdDelete = new LineDeleteCommand(0);
        this.cmdPattern = cmdPattern;
    }

    public StreamEditor() {
        this.cmdDelete = new LineDeleteCommand(0);
        this.cmdPattern = new PatternCommand(Pattern.compile(""));
    }

    private Action getOrder(LineNumberReader text, String current) {
        var match = cmdPattern.pattern().matcher(current);
        if (text.getLineNumber() != cmdDelete.line() && !match.matches()) {
            return Action.PRINT;
        } else {
            return Action.DELETE;
        }
    }

    public void transform(LineNumberReader text, Writer write) throws IOException {
        Objects.requireNonNull(text);
        Objects.requireNonNull(write);
        var current = text.readLine();
        while (current != null) {
            //if (text.getLineNumber() != delete.line()) {
            if (getOrder(text, current).equals(Action.PRINT)) {
                write.append(current).append("\n");
            }
            current = text.readLine();
        }
    }

    public static LineDeleteCommand lineDelete(int line) {
        if (line < 1) {
            throw new IllegalArgumentException();
        }
        return new LineDeleteCommand(line);
    }

    public static PatternCommand findAndDelete(Pattern pattern) {
        Objects.requireNonNull(pattern);
        return new PatternCommand(pattern);
    }

    public static void main(String[] args) {
        try(var file = Files.newBufferedReader(Path.of(args[0]));
        var writer = new OutputStreamWriter(System.out)) {
            var command = StreamEditor.lineDelete(2);
            var editor = new StreamEditor(command);
            editor.transform(new LineNumberReader(file), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
