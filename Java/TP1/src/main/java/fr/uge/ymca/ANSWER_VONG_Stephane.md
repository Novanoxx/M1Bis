### VONG Stéphane (groupe apprenti 2)
# TP1 Java 2022-2023
## YMCA

1.
Écrire le code de VillagePeople tel que l'on puisse créer des VillagePeople avec leur nom et leur sorte. Par exemple,
```
var lee = new VillagePeople("Lee", Kind.BIKER);
System.out.println(lee);  // Lee (BIKER)
```
L'affichage d'un VillagePeople affiche son nom, puis sa sorte entre parenthèses comme dans l'exemple ci-dessus.
Vérifier que les tests JUnit marqués "Q1" passent.
Attention, il y a toujours un problème avec les tests, si ceux-ci passent, on ne sait pas si le code est correct, car il peut aussi manquer un test qui va montrer que le code n'est pas correct. On sait juste que le code n'est pas bon quand les tests ne marchent pas.

<b>Réponse:</b>
J'ai crée un record VillagePeople avec pour champs un String et un Kind.
```
public record VillagePeople(String name, Kind kind) implements People{
	
	public VillagePeople(String name, Kind kind) {
		this.name = Objects.requireNonNull(name);
		this.kind = Objects.requireNonNull(kind);
	}
	@Override
	public String toString() {
		return name + " (" + kind + ")";
	}
}
```
2. 
On veut maintenant introduire une maison House qui va contenir des VillagePeople. Une maison possède une méthode add qui permet d'ajouter un VillagePeople dans la maison (note : il est possible d'ajouter plusieurs fois le même). L'affichage d'une maison doit renvoyer le texte "House with" suivi des noms des VillagePeople ajoutés à la maison, séparés par une virgule. Dans le cas où une maison est vide, le texte est "Empty House".
```
var house = new House();
System.out.println(house);  // Empty House
var david = new VillagePeople("David", Kind.COWBOY);
var victor = new VillagePeople("Victor", Kind.COP);
house.add(david);
house.add(victor);
System.out.println(house);  // House with David, Victor
```
Vérifier que les tests JUnit marqués "Q2" passent.
Pour l'affichage, vous pouvez utiliser un StringBuilder.
Attention à ce que l'on ne puisse pas ajouter des VillagePeople null !

<b>Réponse:</b>
J'ai crée la classe House qui a un Arraylist qui contient que des VillagePeople. Elle a override toString pour pouvoir afficher
    de la façon demandé les valeurs demandées.
```
@Override
public String toString() {
    if (lst.isEmpty()) {
        return "Empty House";
    }
    StringBuilder builder = new StringBuilder();
    builder.append("House with ");
    for (int i = 0; i < lst.size(); i++) {
        builder.append(lst.get(i).name());
        if (i + 1 != lst.size()) {
            builder.append(", ");
        }
    }
    return builder.toString();
}
```

3. 
En fait on veut que l'affichage affiche les noms des VillagePeople dans l'ordre alphabétique, il va donc falloir trier les noms avant de les afficher. On pourrait créer une liste intermédiaire des noms puis les trier avec un appel à list.sort(null) mais cela commence à faire beaucoup de code pour un truc assez simple. Heureusement, il y a plus simple, on va utiliser un Stream pour faire l'affichage.
Dans un premier temps, ré-écrire le code de l'affichage (commenter l'ancien) pour utiliser un Stream sans se préoccuper du tri et vérifier que les tests de la question précédente passent toujours. Puis demander au Stream de se trier et vérifier que les tests marqués "Q3" passent.
<br><b>Note :</b> pour joindre les éléments d'un Stream qui sont des sous-types de CharSequence on utilise le collector Collectors.joining().
<br><b>Note 2 :</b> Lorsque l'on a une modification à effectuer, la démarche qui consiste dans un premier temps à changer le code en gardant l'ancien mode de fonctionnement puis introduire le nouveau mode de fonctionnement s'appelle faire du refactoring. On prépare le terrain pour ajouter la feature sans casser le code existant, puis on ajoute la nouvelle feature.

<b>Réponse:</b>
```
@Override
public String toString() {
    if (lst.isEmpty()) {
        return "Empty House";
    }
    return "House with " + lst.stream().map(People::name).sorted().collect(Collectors.joining(", "));
}
```

4.
En fait, avoir une maison qui ne peut accepter que des VillagePeople n'est pas une bonne décision en termes de business, ils ne sont pas assez nombreux. YMCA décide donc qu'en plus des VillagePeople ses maisons permettent maintenant d'accueillir aussi des Minions, une autre population sans logement.
On souhaite donc ajouter un type Minion (qui possède juste un nom name) et changer le code de House pour permettre d'ajouter des VillagePeople ou des Minion. Un Minion affiche son nom suivi entre parenthèse du texte "MINION".
<br>Vérifier que les tests JUnit marqués "Q4" passent.
<br><b>Note :</b> on souhaite qu'il soit possible d'ajouter dans le futur d'autres personnes que des VillagePeople et des Minion...

<b>Réponse:</b>
J'ai crée un record Minion avec uniquement un String et une interface People qui sera implémenté dans VillagePeople et Minions.

5. 
On cherche à ajouter une méthode averagePrice à House qui renvoie le prix moyen pour une nuit sachant que le prix pour une nuit pour un VillagePeople est 100 et le prix pour une nuit pour un Minion est 1 (il vaut mieux être du bon côté du pistolet à prouts). Le prix moyen (renvoyé par averagePrice) est la moyenne des prix des VillagePeople et Minion présent dans la maison.
Écrire la méthode averagePrice en utilisant le polymorphisme (late dispatch) pour trouver le prix de chaque VillagePeople ou Minion.
<br>Vérifier que les tests JUnit marqués "Q5" passent.
<br><b>Note :</b> dans le cas où la maison est vide, on renverra NaN (Not A Number).

<b>Réponse:</b>
```
public double averagePrice() {
    if (lst.isEmpty()) {
        return Double.NaN;
    }
    float avg = 0;
    for (var people : lst) {
        switch(people) {
        case VillagePeople village -> avg += 100;
        case Minion minion -> avg += 1;
        case default -> throw new IllegalStateException();
        }
    }
    return avg/lst.size();
}
```

6.
En fait, cette implantation n'est pas satisfaisante car elle ajoute une méthode publique dans VillagePeople et Minion alors que c'est un détail d'implantation. Au lieu d'utiliser la POO (programmation orienté objet), on va utiliser la POD (programmation orienté data) qui consiste à utiliser le pattern matching pour connaître le prix par nuit d'un VillagePeople ou un Minion.
Modifier votre code pour introduire une méthode privée qui prend en paramètre un VillagePeople ou un Minion et renvoie son prix par nuit puis utilisez cette méthode pour calculer le prix moyen par nuit d'une maison.
<br>Vérifier que les tests JUnit marqués "Q6" passent.
<br><b>Note:</b> le pattern matching, le switch sur des objets est une preview feature qui doit être activé à la compilation ainsi qu'à l'exécution.

<b>Réponse:</b>
Regarder la question 5.

7.
L'implantation précédente pose problème : il est possible d'ajouter une autre personne qu'un VillagePeople ou un Minion, mais celle-ci ne sera pas prise en compte par le pattern matching. Pour cela, on va interdire qu'une personne soit autre chose qu'un VillagePeople ou un Minion en scellant le super type commun.
<br>Faite les changements qui s'imposent et vérifier que les tests JUnit marqués "Q7" passent.
<br><b>Note :</b> il ne devrait pas y avoir de cas default dans votre switch !

<b>Réponse:</b><br>
J'ai ajouté les propriétés sealed et permits VillagePeople et Minion, puis j'ai ajusté averagePrice().

<b>Réponse:</b>
nouvelle version de l'interface People:
```
sealed interface People permits VillagePeople, Minion{
	public String name();
}
```
nouvelle version de averagePrice():
```
public double averagePrice() {
    if (lst.isEmpty()) {
        return Double.NaN;
    }
    float avg = 0;
    for (var people : lst) {
        //avg += people.price();
        switch(people) {
        case VillagePeople village -> avg += 100;
        case Minion minion -> avg += 1;
        }
    }
    return avg/lst.size();
}
```
8.
On veut périodiquement faire un geste commercial pour une maison envers une catégorie/sorte de VillagePeople en appliquant une réduction de 80% pour tous les VillagePeople ayant la même sorte (par exemple, pour tous les BIKERs). Pour cela, on se propose d'ajouter une méthode addDiscount qui prend une sorte en paramètre et offre un discount pour tous les VillagePeople de cette sorte. Si l'on appelle deux fois addDiscount avec la même sorte, le discount n'est appliqué qu'une fois.
<br>Implanter la méthode addDiscount et vérifier que les tests JUnit marqués "Q8" passent.
<br>Attention à l'algo, les discounts ne doivent pas être stockés dans une liste, il y a plus efficace !

<b>Réponse:</b>
On ajoute une hashset <i>discounted</i> qui contiendra tout les types qui auront un rabais.
```
private final HashSet<Kind> discounted = new HashSet<>();

public void addDiscount(Kind kind) {
    Objects.requireNonNull(kind);
    discounted.add(kind);
}
```
9.
Enfin, on souhaite pouvoir supprimer l'offre commerciale (discount) en ajoutant la méthode removeDiscount qui supprime le discount si celui-ci a été ajouté précédemment ou plante s'il n'y a pas de discount pour la sorte prise en paramètre.
<br>Implanter la méthode removeDiscount et vérifier que les tests JUnit marqués "Q9" passent.

<b>Réponse:</b>
```
public void removeDiscount(Kind kind) {
    Objects.requireNonNull(kind);
    if (!discounted.remove(kind)) {
        throw new IllegalStateException();
    }
}
```