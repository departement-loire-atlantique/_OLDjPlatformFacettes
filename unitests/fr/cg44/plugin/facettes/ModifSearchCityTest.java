package fr.cg44.plugin.facettes;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jalios.jcms.Publication;
import com.jalios.jcms.QueryResultSet;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.jcms.test.JcmsTestCase4;

import fr.cg44.plugin.facettes.queryfilter.CityQueryFilter;
import generated.Canton;
import generated.City;
import generated.Place;

/**
 * Test unitaire de la classe IndexationDataController pour la ré-indexation après modification d'une commune
 * Et de la classe CityQueryFilter pour la recherche sur le code commune
 */
public class ModifSearchCityTest extends JcmsTestCase4  {

	/**
	 * TU 1 recherchePubDirectCommune
	 */
	static City city1_1;
	static City city1_2;
	static Place place1_1;
	static Place place1_2;
	static Place place1_3;
	
	@BeforeClass
	/**
	 * Création des données de test pour chaque test unitaire
	 */
	public static void avantTest() {		
		
				
		// Données test 1
		createDataRecherchePubDirectCommune();
		
		
		// L'indexation des publications ayant lieu de manière asynchrone dans un thread dédié.
		// la recherche textuelle d'une publication immédiatement après sa création pourrait ne pas renvoyer de résultat.
		// Il est alors nécessaire de faire appel à une temporisation.
		try { Thread.sleep(1000); } catch(Exception ex) { }	
		
		// Modification données test 1
		modifDataRecherchePubDirectCommune();
		
		
		// L'indexation des publications ayant lieu de manière asynchrone dans un thread dédié.
		// la recherche textuelle d'une publication immédiatement après sa création pourrait ne pas renvoyer de résultat.
		// Il est alors nécessaire de faire appel à une temporisation.
		try { Thread.sleep(1000); } catch(Exception ex) { }	
		
	}
	
	

	/**
	 * Données test 1
	 * Données de test pour recherchePubDirectCommune
	 */
	private static void createDataRecherchePubDirectCommune() {
		/**
		 * Commune
		 */		
		// Création d'une commune de test "commune 1.1"
		city1_1 = new City();
		city1_1.setTitle("Commune 1.1");
		city1_1.setCityCode(1100);
		city1_1.setAuthor(admin);
		city1_1.performCreate(admin);	
		
		// Création d'une commune de test "commune 1.2"
		city1_2 = new City();
		city1_2.setTitle("Commune 1.2");
		city1_2.setCityCode(1200);
		city1_2.setAuthor(admin);
		city1_2.performCreate(admin);	
				
		/**
		 * Fiche lieux sur Commune 1
		 */	
		// Création d'une fiche lieux de test "place 1.1"
		place1_1 = new Place();
		place1_1.setTitle("place 1.1");
		place1_1.setCity(city1_1);
		place1_1.setAuthor(admin);
		place1_1.performCreate(admin);
		
		// Création d'une fiche lieux de test "place 1.2"
		place1_2 = new Place();
		place1_2.setTitle("place 1.2");
		place1_2.setCity(city1_1);
		place1_2.setAuthor(admin);
		place1_2.performCreate(admin);
		
		/**
		 * Fiche lieux sur Commune 2
		 */
		// Création d'une fiche lieux de test "place 1.3"
		place1_3 = new Place();
		place1_3.setTitle("place 1.3");
		place1_3.setCity(city1_2);
		place1_3.setAuthor(admin);
		place1_3.performCreate(admin);	
	}
	
	
	/**
	 * Modification données test 1
	 * Données de test pour recherchePubDirectCommune
	 */
	private static void modifDataRecherchePubDirectCommune() {
		// Modification du code commune de la commune 1.1
		City clone1_1 = (City) city1_1.getUpdateInstance();
		clone1_1.setCityCode(1105);
		clone1_1.performUpdate(admin);
	}
	
	
	
	@Test
	/**
	 * Test recherche sur commune 1.1
	 * Après modification du code commune de la commune recherchée
	 * Test sur les publications qui référencent directement la commune (champ mono)
	 */
	public void recherchePubDirectCommune1_1() {		
		// Recherche sur commune 3.1
		// city 3.1 est référencée par : place 3.1 et place 3.2
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place1_1);
		resultatTestSet.add(place1_2);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city1_1);
		qh.setTypes("Place");		
	    QueryResultSet qrs = qh.getResultSet();	    	    
	    assertEquals("Recherche sur commune 3.1 invalide", resultatTestSet, qrs);	
	}

	
}
