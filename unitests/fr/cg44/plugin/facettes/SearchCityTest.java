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
 * Test unitaire de la classe PublicationFacetedSearchCityEnginePolicyFilter pour la ré-indexation complète
 * Et de la classe CityQueryFilter pour la recherche sur le code commune
 */
public class SearchCityTest extends JcmsTestCase4  {

	/**
	 * TU 1 recherchePubDirectCommune
	 */
	static City city1_1;
	static City city1_2;
	static Place place1_1;
	static Place place1_2;
	static Place place1_3;
	
	/**
	 * TU 2 recherchePubRefCantonCommune
	 */
	static Canton canton2_1;
	static Canton canton2_2;
	static City city2_1;
	static City city2_2;
	static City city2_3;
	static Place place2_1;
	static Place place2_2;
	static Place place2_3;
	
	/**
	 * TU 3 recherchePubRefInverseCantonCommune
	 */
	static Canton canton3_1;
	static Canton canton3_2;
	static Canton canton3_3;
	static Canton canton3_4;
	static City city3_1;
	static City city3_2;
	static Place place3_1;
	static Place place3_2;
	static Place place3_3;
	static Place place3_4;
	
	
	@BeforeClass
	/**
	 * Création des données de test pour chaque test unitaire
	 */
	public static void avantTest() {		
		// Données de test pour recherchePubDirectCommune
		createDataRecherchePubDirectCommune();		
		// Données de test pour recherchePubRefCantonCommune
		createDataRecherchePubRefCantonCommune();	
		// Données de test pour recherchePubRefInverseCantonCommune
		createDataRecherchePubRefInverseCantonCommune();
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
		city1_1.setCityCode(110);
		city1_1.setAuthor(admin);
		city1_1.performCreate(admin);	
		
		// Création d'une commune de test "commune 1.2"
		city1_2 = new City();
		city1_2.setTitle("Commune 1.2");
		city1_2.setCityCode(120);
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
	 * Données test 2
	 * Données de test pour recherchePubRefCantonCommune
	 */
	private static void createDataRecherchePubRefCantonCommune() {
		
		/**
		 * Canton
		 */
		// Création d'une canton de test "canton 2.1"
		canton2_1 = new Canton();
		canton2_1.setTitle("Canton 2.1");
		canton2_1.setAuthor(admin);
		canton2_1.performCreate(admin);	
		
		// Création d'une canton de test "canton 2.2"
		canton2_2 = new Canton();
		canton2_2.setTitle("Canton 2.2");
		canton2_2.setAuthor(admin);
		canton2_2.performCreate(admin);	
		
		/**
		 * Commune avec canton 2.1
		 */		
		// Création d'une commune de test "commune 2.1"
		city2_1 = new City();
		city2_1.setTitle("Commune 2.1");
		city2_1.setCityCode(210);
		city2_1.setCanton(canton2_1);
		city2_1.setAuthor(admin);
		city2_1.performCreate(admin);	
		
		// Création d'une commune de test "commune 2.2"
		city2_2 = new City();
		city2_2.setTitle("Commune 2.2");
		city2_2.setCityCode(220);
		city2_2.setCanton(canton2_1);
		city2_2.setAuthor(admin);
		city2_2.performCreate(admin);	
		
		/**
		 * Commune avec canton 2.2
		 */	
		// Création d'une commune de test "commune 2.3"
		city2_3 = new City();
		city2_3.setTitle("Commune 2.3");
		city2_3.setCityCode(230);
		city2_3.setCanton(canton2_2);
		city2_3.setAuthor(admin);
		city2_3.performCreate(admin);		
		
		/**
		 * Fiche lieux sur Canton 2.1
		 */	
		// Création d'une fiche lieux de test "place 2.1"
		place2_1 = new Place();
		place2_1.setTitle("place 2.1");
		place2_1.setCanton(canton2_1);
		place2_1.setAuthor(admin);
		place2_1.performCreate(admin);
		
		// Création d'une fiche lieux de test "place 2.2"
		place2_2 = new Place();
		place2_2.setTitle("place 2.2");
		place2_2.setCanton(canton2_1);
		place2_2.setAuthor(admin);
		place2_2.performCreate(admin);
		
		/**
		 * Fiche lieux sur Canton 2.2
		 */	
		// Création d'une fiche lieux de test "place 2.3"
		place2_3 = new Place();
		place2_3.setTitle("place 2.3");
		place2_3.setCanton(canton2_2);
		place2_3.setAuthor(admin);
		place2_3.performCreate(admin);
		
	}
	
	/**
	 * Données test 3
	 * Données de test pour recherchePubRefInverseCantonCommune
	 */
	private static void createDataRecherchePubRefInverseCantonCommune() {
		
		/**
		 * Commune
		 */		
		// Création d'une commune de test "commune 3.1"
		city3_1 = new City();
		city3_1.setTitle("Commune 3.1");
		city3_1.setCityCode(310);
		city3_1.setAuthor(admin);
		city3_1.performCreate(admin);	
		
		// Création d'une commune de test "commune 3.2"
		city3_2 = new City();
		city3_2.setTitle("Commune 3.2");
		city3_2.setCityCode(320);
		city3_2.setAuthor(admin);
		city3_2.performCreate(admin);	
		
		/**
		 * Canton sur la commune 3.1
		 */
		// Création d'une canton de test "canton 3.1"
		canton3_1 = new Canton();
		canton3_1.setTitle("Canton 3.1");
		canton3_1.setCity(city3_1);
		canton3_1.setAuthor(admin);
		canton3_1.performCreate(admin);	
		
		// Création d'une canton de test "canton 3.2"
		canton3_2 = new Canton();
		canton3_2.setTitle("Canton 3.2");
		canton3_2.setCity(city3_1);
		canton3_2.setAuthor(admin);
		canton3_2.performCreate(admin);	
		
		/**
		 * Canton sur la commune 3.2
		 */
		// Création d'une canton de test "canton 3.3"
		canton3_3 = new Canton();
		canton3_3.setTitle("Canton 3.3");
		canton3_3.setCity(city3_2);
		canton3_3.setAuthor(admin);
		canton3_3.performCreate(admin);	
		
		// Création d'une canton de test "canton 3.4"
		canton3_4 = new Canton();
		canton3_4.setTitle("Canton 3.4");
		canton3_4.setCity(city3_2);
		canton3_4.setAuthor(admin);
		canton3_4.performCreate(admin);			
		
		/**
		 * Fiche lieux sur Canton 3.1
		 */	
		// Création d'une fiche lieux de test "place 3.1"
		place3_1 = new Place();
		place3_1.setTitle("place 3.1");
		place3_1.setCanton(canton3_1);
		place3_1.setAuthor(admin);
		place3_1.performCreate(admin);
		
		/**
		 * Fiche lieux sur Canton 3.2
		 */	
		// Création d'une fiche lieux de test "place 3.2"
		place3_2 = new Place();
		place3_2.setTitle("place 3.2");
		place3_2.setCanton(canton3_2);
		place3_2.setAuthor(admin);
		place3_2.performCreate(admin);
		
		/**
		 * Fiche lieux sur Canton 3.3
		 */	
		// Création d'une fiche lieux de test "place 3.3"
		place3_3 = new Place();
		place3_3.setTitle("place 3.3");
		place3_3.setCanton(canton3_3);
		place3_3.setAuthor(admin);
		place3_3.performCreate(admin);
		
		/**
		 * Fiche lieux sur Canton 3.4
		 */	
		// Création d'une fiche lieux de test "place 3.4"
		place3_4 = new Place();
		place3_4.setTitle("place 3.4");
		place3_4.setCanton(canton3_4);
		place3_4.setAuthor(admin);
		place3_4.performCreate(admin);
		
	}


	/*-----------------------------------------------------------------------------------/
	 * Test 1
	 * Fiche lieux -> Commune
	 * Test de recherche sur des fiches lieux
	 * Les fiches lieux référencement directement la commune dans leur champ "commune"
	 *-----------------------------------------------------------------------------------*/

	@Test
	/**
	 * Test recherche sur commune 1.1
	 */
	public void recherchePubDirectCommune1_1() {
			
		// Recherche sur commune 1.1
		// city 1.1 est référencée par : place 1.1 et place 1.2
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place1_1);
		resultatTestSet.add(place1_2);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city1_1);
		qh.setTypes("Place");		
		QueryResultSet qrs = qh.getResultSet();	    	    
		assertEquals("Recherche sur commune 1.1 invalide", resultatTestSet, qrs);
	}

	@Test
	/**
	 * Test recherche sur commune 1.2
	 */
	public void recherchePubDirectCommune1_2() {
		// Recherche sur commune 1.2
		// city 1.2 est référencée par : place 1.3
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place1_3);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city1_2);
		qh.setTypes("Place");		
		QueryResultSet qrs = qh.getResultSet();	        
		assertEquals("Recherche sur commune 1.2 invalide", resultatTestSet, qrs); 
	}
	
	
	
	
	/*-----------------------------------------------------------------------------------/
	 * Test 2
	 * Fiche Lieux -> Canton <- Commune
	 * Test de recherche sur des fiches lieux
	 * Les fiches lieux référencement indirectement la commune dans leur champ "canton" (le canton référence la commune)
	 * Chaque commune référence son canton (exemple réel : ((Commune) Saint-Lumine-de-Clisson, Clisson, Gétigné) -> ((Canton) Clisson) )
	 *-----------------------------------------------------------------------------------*/
	
	@Test
	/**
	 * Test recherche sur commune 2.1
	 */
	public void recherchePubRefCantonCommune2_1() {			
		// Recherche sur commune 2.1
		// city 2.1 est référencée par : place 2.1 et place 2.2
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place2_1);
		resultatTestSet.add(place2_2);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city2_1);
		qh.setTypes("Place");		
	    QueryResultSet qrs = qh.getResultSet();	    	    
	    assertEquals("Recherche sur commune 2.1 invalide", resultatTestSet, qrs);		    
	}
	
	@Test
	/**
	 * Test recherche sur commune 2.2
	 */
	public void recherchePubRefCantonCommune2_2() {			
		// Recherche sur commune 2.2
		// city 2.2 est référencée par : place 2.1 et place 2.2
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place2_1);
		resultatTestSet.add(place2_2);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city2_2);
		qh.setTypes("Place");		
	    QueryResultSet qrs = qh.getResultSet();	    	    
	    assertEquals("Recherche sur commune 2.2 invalide", resultatTestSet, qrs);		    
	}
	
	@Test
	/**
	 * Test recherche sur commune 2.2
	 */
	public void recherchePubRefCantonCommune2_3() {			
		// Recherche sur commune 2.2
		// city 2.3 est référencée par : place 2.3
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place2_3);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city2_3);
		qh.setTypes("Place");		
	    QueryResultSet qrs = qh.getResultSet();	    	    
	    assertEquals("Recherche sur commune 2.3 invalide", resultatTestSet, qrs);		    
	}
	
	
	
	
	/*-----------------------------------------------------------------------------------/
	 * Test 3
	 * Fiche Lieux -> Canton -> Commune
	 * Test de recherche sur des fiches lieux
	 * Les fiches lieux référencement indirectement la commune dans leur champ "canton"
	 * La commune ne référence PAS son canton
	 * Le canton référence sa commune (exemple réel : ((Canton) Nantes 1, Nantes 2, nantes 3 etc.) -> ((Commune) Nantes) )
	 *-----------------------------------------------------------------------------------*/
	
	@Test
	/**
	 * Test recherche sur commune 3.1
	 */
	public void recherchePubRefInverseCantonCommune3_1() {		
		// Recherche sur commune 3.1
		// city 3.1 est référencée par : place 3.1 et place 3.2
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place3_1);
		resultatTestSet.add(place3_2);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city3_1);
		qh.setTypes("Place");		
	    QueryResultSet qrs = qh.getResultSet();	    	    
	    assertEquals("Recherche sur commune 3.1 invalide", resultatTestSet, qrs);	
	}
	
	@Test
	/**
	 * Test recherche sur commune 3.2
	 */
	public void recherchePubRefInverseCantonCommune3_2() {		
		// Recherche sur commune 3.2
		// city 3.2 est référencée par : place 3.3 et place 3.4
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place3_3);
		resultatTestSet.add(place3_4);
		QueryHandler qh = new QueryHandler();
		CityQueryFilter.addCitySearch(qh, city3_2);
		qh.setTypes("Place");		
	    QueryResultSet qrs = qh.getResultSet();	    	    
	    assertEquals("Recherche sur commune 3.2 invalide", resultatTestSet, qrs);	
	}
	

	
}
