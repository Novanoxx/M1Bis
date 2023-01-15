package fr.upem.net.udp.nonblocking;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class AnswerLog {
    private final BitSet answers;
    private final int nbLines;
    private int count = 0;

    public AnswerLog(int nbLines) {
        this.answers = new BitSet(nbLines);
        this.nbLines = nbLines;
    }

    boolean isFinished() {
        return count == nbLines;
    }

    void update(int lineNumber) {
        if (!answers.get(lineNumber)) {
            answers.set(lineNumber);
            count++;
        }
    }

    boolean get(int id) {
        return answers.get(id);
    }

    List<Integer> missingAnswer() {
        var lst = new ArrayList<Integer>();
        for (var i = 0; i < nbLines; i++) {
            if (!answers.get(i)) {
                lst.add(i);
            }
        }
        return lst;
    }
}
