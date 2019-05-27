## Portlet FacetedSearch

### Objectif

Pouvoir réaliser simplement des moteurs à facette gràce à une Portlet dédiée
TODO : Explication de laportlet, voir si tout sert encore
TODO : Analyse de tous les cas - Rappel des fonctionnalités

Fonction panier : actif ou non
Fonction export CVS panier : actif ou non
Fonction export CSV de la liste courante : actif ou non
Fonction export PDF panier : actif ou non
Fonction export PDF de la liste courante : actif ou non

Colonnes CSV (Le CVS présentera tous les champs entre guillements) :
  - url : URL du contenu (généré par JCMS)
  - * : nom technique du champ
  - multivalué : séparé les données par des points virgules
  - x.y : le champs y du contenu x
  - en cas d'erreur (le champs n'existe pas, exception, ... vide)
  
Exemple : 



Config :

Titre colonne CSV : ["titre", "teléphone", "URL du document"; ...]
Titre général PDF : "titre PDF"

places:
 - csv usage : Nom de l'usage
 - Colonnes CSV : [title, telephone, document.url, url]
 - Titre contenu PDF : $[titre] - $[date_creation]
 - pdf usage : Nom de l'usage
 - text PDF (champ texte) :
 
**Adresse** : ${adresse}
**tel** : ${tel}
**canton**: ${canton.title}
 - infobulle usage : Nom de l'usage
 - titre infobulle : ${title}
 - lien infobulle: url
 - picto infobulle: url | id_cat|url_cat, id_cat|url_cat, id_cat|couleur
 - text infobulle (champ texte) :
**Adresse** : ${adresse}
**tel** : ${tel}
**canton**: ${canton.title}
 - card usage : Nom de l'usage
 - titre card
 - tag card
 - couleur card :  id_cat|url_cat, id_cat|couleur
 - text card
 - lien card: url
 - image card
 - minimap long lat
 - ...

canton:
 - Colonnes CSV : [title, _, _, url]
 - Titre contenu PDF : $[titre]
 - text PDF (champ texte) :
**Adresse** : ${adresse} 


Pour chaque type de contenu : valeur par défaut pouvant être surchargé au niveau de la portlet


### Fichiers impliqués 

types/PortletFacetedSearch/doPortletFacetedSearch.jsp
TODO : avoir un gabarit qui prend en compte toutes les fonctionnalités 

WEB-INF/data/types/PortletFacetedSearch/PortletFacetedSearch.xml
TODO : a reevoir

## QueryFilter et indexation personnalisés des types de contenu Canton et Ville

TODO : communes limitrophes à ajouter dans les facettes
TODO :
 - CantonsFacetQueryFilter.java => Quel cas ?
 - CitiesFacetQueryFilter.java => A faire
 - CityFacetQueryFilter.java => Fait (reste à valider les communes limitrophes)
 - DateFacetQueryFilter.java => A faire - Rendu: Elle sera probablement modifée (date + période : le j1our même, le prochain, les prochains, le WE prochain)
 - DelegationsFacetQueryFilter.java => A faire
 - FullTextFacetQueryFilter.java -> Dans tous les champs indexés du contenu
   -- Revoir pondération des champs (configurable : reste à savoir comment)
 
### Objectif

 - Quand un contenu est associé à un canton, alors il est automatiquement associé à toutes les villes du canton.
 - Quand un contenu est associé à une commune, alors le code INSEE de la commune est ajouté aux meta donnée d'indexation du contenu.
 - Quand un contenu est associé à une délégation, alors le code postal de la délégation est ajouté au meta donnée d'indexation du contenu. La recherche sur une délagation va aussi lancer la recherche sur les communes qui ont un lien avec la délégation recherchée.

Le datacontroleur "IndexationDataController" détecte si il est nécessaire de mettre à jour l'indexation si un contenu ville, canton ou délégation a été modifié (code INSEE ou le lien vers canton de la ville). Ce controleur ne réindexe que les publications impactées.

La classe java LuceneQueryFilter est un classe abstraite dont doit hériter tous les query filter de la recherche à facettes. Elle permet d'initialiser la variable "HttpServletRequest request" et de vérifier si celle-ci n'est pas null (se produit lors du démarrage de JPlatform). Il faut surcharger la méthode "doFilter" pour les classes filles qui hérite de cette classe et non "filterQueryHandler".

Le query filter canton, ville et délégation permet d'utiliser cette indexation personnalisée dans les requêtes. Dès que la recherche implique un contenu canton, commune ou délégation, la recherche se base automatiquement sur les champs lucene personnalisés et indexés spécifiquement.

Le query filter délégation en plus de rechercher dans les publications qui référence directement la délégation, cherche aussi dans les publications qui référence une ou plusieurs communes qui référence elle même la délégation. (Publication -> Commune -> Délégation). Pour effectuer la recherche sur les communes de la délégation, le query filter se base sur le champs lucene personnalisé des communes (code commune).

### Fichiers impliqués

WEB-INF/classes/fr/cg44/plugin/socle/indexation/policyfilter/PublicationFacetedSearchCantonEnginePolicyFilter.java
WEB-INF/classes/fr/cg44/plugin/socle/indexation/policyfilter/PublicationFacetedSearchCityEnginePolicyFilter.java
WEB-INF/classes/fr/cg44/plugin/socle/indexation/datacontroller/IndexationDataController.java
WEB-INF/classes/fr/cg44/plugin/facettes/queryfilter/LuceneQueryFilter.java
WEB-INF/classes/fr/cg44/plugin/facettes/queryfilter/CantonQueryFilter.java
WEB-INF/classes/fr/cg44/plugin/facettes/queryfilter/CityQueryFilter.java
WEB-INF/classes/fr/cg44/plugin/facettes/queryfilter/DelegationQueryFilter.java
TODO - A finir et à décrire WEB-INF/classes/fr/cg44/plugin/facettes/queryfilter/TitleQueryFilter.java
TOD - faire une classe mère pour les filter - Nom LuceneQueryFilter (Abstraite avec nouvelle méthode)

Tests unitaires pour l'indexation :
unitests/fr/cg44/plugin/facettes/ModifSearchCityTest.java
unitests/fr/cg44/plugin/facettes/SearchCityTest.java
unitests/fr/cg44/plugin/facettes/SocleDataInit.java
TODO : A relire


## Surcharge des query handler des portlets "Portlet requête itération" et "Portlet itération détaillée"

### Objectif

Pouvoir gérer la recherche par catégorie / sous catégories avec par défaut les règles suivantes dès que la requête contient un ou des paramètres cidBranches :
 - union entre les éléments d'une même branche (cids)
 - intersection entre les éléments de différentes branches(cidBranches)
 - si on sélectionne un enfant et son parent alors le parent est automatiquement ignoré
  
Pour rappel, nativement, JCMS aurait simplement réalisé une requête en mode union sur toutes les catégories.

### Fichiers impliqués

- types/PortletQueryForeach/doQuery.jspf
- types/PortletQueryForeach/doQueryHandlers.jspf
 
