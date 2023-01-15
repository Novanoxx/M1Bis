### VONG Stéphane (groupe apprenti 2)
# TP4 Java 2022-2023
## Exercice 2 - JSON Encoder

1. Écrire la méthode toJSON qui prend en paramètre un java.lang.Record, utilise la réflexion pour accéder à l'ensemble des composants d'un record (java.lang.Class.getRecordComponent), sélectionne les accesseurs, puis affiche les couples nom du composant, valeur associée.
   Puis vérifier que les tests marqués "Q1" passent.
<br>Note : il est recommandé d'écrire la méthode en utilisant un Stream.
<br>Note 2 : il faut faire attention à gérer correctement les exceptions lors de l'invocation de méthode (surtout InvocationTargetException).
    ```
          ...
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
    ```
    Note 3 : il y a une petite subtilité avec les guillemets. Dans le format JSON, les chaînes de caractères apparaissent entre "". Nous vous offrons la méthode suivante pour gérer cela :
    ```
    private static String escape(Object o) {
        return o instanceof String s ? "\"" + s + "\"": "" + o;
    }
    ```
   
<b>Réponse:</b>
```
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

public static String toJSON(Record record) {
    var joiner = new StringJoiner(",\n", "{", "}");
    Arrays.stream(record.getClass().getRecordComponents())
            .forEach(elem -> {
                var fields = invoke(elem.getAccessor(), record);
                joiner.add(escape(elem.getAccessor().getName()) + ":" + escape(fields));
            });
    return joiner.toString();
}
```

2. En fait, on peut avoir des noms de clé d'objet JSON qui ne sont pas des noms valides en Java, par exemple "book-title", pour cela on se propose d'utiliser un annotation pour indiquer quel doit être le nom de clé utilisé pour générer le JSON.
<br>Déclarez l'annotation JSONProperty visible à l'exécution et permettant d'annoter des composants de record, puis modifiez le code de toJSON pour n'utiliser que les propriétés issues de méthodes marquées par l'annotation JSONProperty.
<br>Puis vérifier que les tests marqués "Q2" passent (et uniquement ceux-là pour l'instant).

<b>Réponse:</b>
```
private static String nameValue(RecordComponent rc) {
    var property = rc.getAnnotation(JSONProperty.class);
    if (property == null) {
        return rc.getName();
    }
    return property.value();
}
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
```

3. En fait, on veut aussi gérer le fait que l'annotation peut ne pas être présente et aussi le fait que si l'annotation est présente mais sans valeur spécifiée alors le nom du composant est utilisé avec les '_' réécrits en '-'.
   <br>Modifier le code dans JSONPrinter et la déclaration de l'annotation en conséquence.
   <br>Pour tester, vérifier que tous les tests jusqu'à ceux marqués "Q3" passent.
   <br>Rappel : la valeur par défaut d'un attribut d'une annotation ne peut pas être null.
<b>Réponse:</b>
```
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
```

4. En fait, l'appel à getRecordComponents est lent; regardez la signature de cette méthode et expliquez pourquoi...

<b>Réponse:</b> L'appel a getRecordComponents() est lent car chaque appel vérifie des conditions de sécurité, ce qui ralentie l'exécution.

5. Nous allons donc limiter les appels à getRecordComponents en stockant le résultat de getRecordComponents dans un cache pour éviter de faire l'appel à chaque fois qu'on utilise toJSON.
   Utilisez la classe java.lang.ClassValue pour mettre en cache le résultat d'un appel à getRecordComponents pour une classe donnée.

<b>Réponse:</b>
```
private static ClassValue<RecordComponent[]> CACHE = new ClassValue<RecordComponent[]>() {
   @Override
   protected RecordComponent[] computeValue(Class type) {
      return type.getRecordComponents();
   }
};
```

6. En fait, on peut mettre en cache plus d'informations que juste les méthodes, on peut aussi pré-calculer le nom des propriétés pour éviter d'accéder aux annotations à chaque appel.
   <br>Écrire le code qui pré-calcule le maximum de choses pour que l'appel à toJSON soit le plus efficace possible.
   <br>Indication : quelle est la lettre grecque entre kappa et mu?

<b>Réponse:</b>
```
private static ClassValue<List<Function<Record, String>>> CACHE = new ClassValue<>() {
@Override
   protected List<Function<Record, String>> computeValue(Class<?> type) {
      return Arrays.stream(type.getRecordComponents()).<Function<Record, String>>map(elem -> {
         var nameValue = nameValue(elem);
         return record -> escape(nameValue) + ": " + escape(invoke(elem.getAccessor(), record));
         }).toList();
   }
};
public static String toJSON(Record record) {
   return CACHE.get(record.getClass()).stream().map(func -> func.apply(record)).collect(Collectors.joining(", ", "{", "}"));
}
```