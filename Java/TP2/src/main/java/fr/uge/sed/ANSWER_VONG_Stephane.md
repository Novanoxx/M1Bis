### VONG Stéphane (groupe apprenti 2)
# TP2 Java 2022-2023
## Sed

1. Dans un premier temps, on va créer une classe StreamEditor dans le package fr.uge.sed avec une méthode d'instance transform qui prend en paramètre un LineNumberReader et un Writer et écrit, ligne à ligne, le contenu du LineNumberReader dans le Writer.
   Rappel, un BufferedReader possède une méthode readLine() et un Writer une méthode append().
   Comme on veut que le programme fonctionne de la même façon, quelle que soit la plate-forme, le retour à la ligne écrit dans le Writer est toujours '\n'.
   <br>Vérifier que les tests JUnit marqués "Q1" passent.

<b>Réponse:</b>
```
public void transform(LineNumberReader text, Writer write) {
    Objects.requireNonNull(text);
    Objects.requireNonNull(write);
    var current = text.readLine();
    while (current != null) {
        write.append(current).append("\n");
        current = text.readLine();
    }
}
```
2. On veut maintenant pouvoir spécifier une commande à la création du StreamEditor pour transformer les lignes du fichier en entrée. Ici, lineDelete renvoie un record LineDeleteCommand qui indique la ligne à supprimer (la première ligne d'un fichier est 1, pas 0).
   L'exemple ci-dessous montre comment supprimer la ligne 2 d'un fichier.
   ```
   var command = StreamEditor.lineDelete(2);
   var editor = new StreamEditor(command);
   editor.transform(reader, writer);
   ```
   L'idée est la suivante : on parcourt le fichier ligne à ligne comme précédemment, mais si le numéro de la ligne courante est égal à celui de la ligne à supprimer, alors on ne l'écrit pas dans le Writer.
   <br>Vérifier que les tests JUnit marqués "Q2" passent.

<b>Réponse:</b>
Après avoir créé un record LineDeleteCommand, <i>transform</i> est modifié en : 
```
public void transform(LineNumberReader text, Writer write) {
    Objects.requireNonNull(text);
    Objects.requireNonNull(write);
    var current = text.readLine();
    while (current != null) {
         if (text.getLineNumber() != delete.line()) {
            write.append(current).append("\n");
         }
         current = text.readLine();
    }
}
```
3. On souhaite maintenant écrire un main qui prend en paramètre sur la ligne de commande un nom de fichier, supprime la ligne 2 de ce fichier et écrit le résultat sur la sortie standard.
   <br>Vérifier que les tests JUnit marqués "Q3" passent.
   <br>Note : on présupposera que le fichier et la sortie standard utilisent l'encodage UTF-8 (StandardCharsets.UTF_8)
   <br>Note 2 : pour transformer un OutputStream (un PrintStream est une sorte d'OutputStream) en Writer, on utilise un OutputStreamWriter et comme on veut spécifier l'encodage, on va utiliser le constructeur qui prend aussi un Charset en paramètre.
   <br>Rappel : vous devez utiliser un try-with-resources pour fermer correctement les ressources ouvertes.

<b>Réponse:</b>
```
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
```

4. On souhaite introduire une nouvelle commande qui permet de supprimer une ligne si elle contient une expression régulière. Avant de le coder, on va faire un peu de re-factoring pour préparer le fait que l'on puisse avoir plusieurs commandes.
   L'idée est que, pour chaque commande, on appelle celle-ci avec la ligne courante et le numéro de ligne spécifié dans la commande, et la commande nous renvoie une action à effectuer. L'action peut être soit DELETE pour indiquer que la ligne ne doit pas être affichée, soit PRINT pour indiquer que la ligne doit être affichée.
   Pour cela, on va utiliser l'enum suivant :
   ```
   enum Action {
       DELETE, PRINT
   }
   ```
   On pourrait utiliser un booléen comme type de retour, mais DELETE/PRINT c'est plus parlant que true/false.
   Changer le code pour que le record LineDeleteCommand possède la méthode décrite plus haut et changer le code de transform pour qu'elle appelle cette méthode.
   <bR>Vérifier que les tests JUnit marqués "Q4" passent.
   <br>Note : au lieu de mettre l'enum dans un fichier .java dans le même package, on va le ranger dans StreamEditor. En Java, mettre un enum, un record ou une interface dans une classe ne pose pas de problème, par contre mettre une classe dans une classe est un peu plus compliqué, on verra ça plus tard.

<b>Réponse:</b> Après avoir ajouté l'enum dans la classe StreamEditor, la méthode <i>getOrder</i> permettra de contenir toutes les conditions qui permettront de renvoyer DELETE/PRINT. 
```
private Action getOrder(LineNumberReader text) {
   if (text.getLineNumber() != cmdDelete.line()) {
      return Action.PRINT;
   } else {
      return Action.DELETE;
   }
}
```

5. Maintenant que l'on a bien préparé le terrain, on peut ajouter une nouvelle commande renvoyée par la méthode findAndDelete qui prend en paramètre un java.util.regex.Pattern telle que le code suivant fonctionne
   ```
   var command = StreamEditor.findAndDelete(Pattern.compile("foo|bar"));
   var editor = new StreamEditor(command);
   editor.transform(reader, writer);
   ```
   Faite les changements qui s'imposent puis vérifier que les tests JUnit marqués "Q5" passent.
   Rappel : pour voir si un texte contient un motif pattern, on instancie un Matcher du motif sur le texte et on utilise la méthode find sur ce Matcher.

<b>Réponse:</b>
```
private Action getOrder(LineNumberReader text, String current) {
   var match = cmdPattern.pattern().matcher(current);
   if (text.getLineNumber() != cmdDelete.line() && !match.find()) {
      return Action.PRINT;
   } else {
      return Action.DELETE;
   }
}
```
6. En fait, cette implantation n'est pas satisfaisante, car les records LineDeleteCommand et FindAndDeleteCommand ont beaucoup de code qui ne sert à rien. Il serait plus simple de les transformer en lambdas, car la véritable information intéressante est comment effectuer la transformation d'une ligne.
   Modifier votre code pour que les implantations des commandes renvoyées par les méthodes lineDelete et findAndDelete soit des lambdas.
   <br>Vérifier que les tests JUnit marqués "Q6" passent