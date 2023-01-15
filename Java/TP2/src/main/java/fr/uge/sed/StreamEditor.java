package fr.uge.sed;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamEditor {
    @FunctionalInterface
    public interface Command {
        Action action(String current, int numLine);
        default Command andThen(Command cmd) {
            Objects.requireNonNull(cmd);
            return (current, numLine) -> switch(action(current, numLine)) {
                                            case Action.PrintAction act -> cmd.action(act.text, numLine);   //Do the other command
                                            case Action.DeleteAction act -> act;    //Do nothing
                                        };
            };
    }

    private sealed interface Action {
        record DeleteAction() implements Action {}
        record PrintAction(String text) implements Action {}
    }
    /*
    private final LineDeleteCommand cmdDelete;
    private final PatternCommand cmdPattern;
     */
    private final Command cmd;
    /*
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
        this.cmdPattern = new PatternCommand(Pattern.compile(" "));
    }

    private Action getOrder(int numLine, String current) {
        var match = cmdPattern.pattern().matcher(current);
        if (numLine != cmdDelete.line() && !match.find()) {
            return Action.PRINT;
        } else {
            return Action.DELETE;
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

     */

    public StreamEditor(Command cmd) {
        Objects.requireNonNull(cmd);
        this.cmd = cmd;
    }

    public StreamEditor() {
        this.cmd = lineDelete(0);
    }

    public void transform(LineNumberReader text, Writer write) throws IOException {
        Objects.requireNonNull(text);
        Objects.requireNonNull(write);
        var current = text.readLine();
        while (current != null) {
            //if (text.getLineNumber() != delete.line()) {
            //if (getOrder(text.getLineNumber(), current).equals(Action.PRINT)) {
            /*
            if (cmd.action(current, text.getLineNumber()).equals(Action.PRINT)) {
                write.append(current).append("\n");
            }
             */
            switch(cmd.action(current, text.getLineNumber())) {
                case Action.PrintAction a -> write.append(a.text).append("\n");
                case Action.DeleteAction a -> {}
            }
            current = text.readLine();
        }
    }

    public static Command lineDelete(int num) {
        if (num < 0) {
            throw new IllegalArgumentException();
        }
        return (current, numLine) -> num != numLine ? new Action.PrintAction(current) : new Action.DeleteAction();
    }
    public static Command findAndDelete(Pattern pattern) {
        Objects.requireNonNull(pattern);
        return (current, numLine) -> pattern.matcher(current).find() ? new Action.DeleteAction() : new Action.PrintAction(current);
    }

    public static Command substitute(Pattern pattern, String replace) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(replace);
        return (current, numLine) -> pattern.matcher(current).find() ? new Action.PrintAction(pattern.matcher(current).replaceAll(replace)) : new Action.PrintAction(current);
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
