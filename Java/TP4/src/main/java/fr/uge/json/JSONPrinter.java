package fr.uge.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JSONPrinter {
    private static ClassValue<List<Function<Record, String>>> CACHE = new ClassValue<>() {
        @Override
        protected List<Function<Record, String>> computeValue(Class<?> type) {
            return Arrays.stream(type.getRecordComponents()).<Function<Record, String>>map(elem -> {
                var nameValue = nameValue(elem);
                return record -> escape(nameValue) + ": " + escape(invoke(elem.getAccessor(), record));
            }).toList();
        }
    };
    /*
    private static ClassValue<RecordComponent[]> CACHE = new ClassValue<RecordComponent[]>() {
        @Override
        protected RecordComponent[] computeValue(Class type) {
            return type.getRecordComponents();
        }
    };

    public static String toJSON(Person person) {
        return """
      {
        "firstName": "%s",
        "lastName": "%s"
      }
      """.formatted(person.firstName(), person.lastName());
    }

    public static String toJSON(Alien alien) {
        return """
      {
        "age": %s,
        "planet": "%s"
      }
      """.formatted(alien.age(), alien.planet());
    }
     */
    private static String escape(Object o) {
        return o instanceof String ? "\"" + o + "\"": "" + o;
    }

    private static Object invoke(Method accessor, Object o) {
        try {
            return accessor.invoke(o);
        } catch (IllegalAccessException e) {
            throw (IllegalAccessError) new IllegalAccessException().initCause(e);
        } catch (InvocationTargetException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException exception) {
                throw exception;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new UndeclaredThrowableException(e);
        }
    }

    private static String nameValue(RecordComponent rc) {
        var property = rc.getAnnotation(JSONProperty.class);
        if (property == null) {
            return rc.getName();
        }
        var value = property.value();
        if (value.isEmpty()) {
            return rc.getName().replace('_', '-');
        }
        return value;
    }
    /*
    public static String toJSON(Record record) {
        var joiner = new StringJoiner(",\n", "{", "}");
        Arrays.stream(record.getClass().getRecordComponents())
                .forEach(elem -> {
                    var nameValue = nameValue(elem);
                    var fields = invoke(elem.getAccessor(), record);
                    joiner.add(escape(nameValue) + ":" + escape(fields));
                });
        return joiner.toString();
    }
     */
    public static String toJSON(Record record) {
        return CACHE.get(record.getClass()).stream().map(func -> func.apply(record)).collect(Collectors.joining(", ", "{", "}"));
    }

    public static void main(String[] args) {
        var person = new Person("John", "Doe");
        System.out.println(toJSON(person));
        var alien = new Alien(100, "Saturn");
        System.out.println(toJSON(alien));
    }
}
