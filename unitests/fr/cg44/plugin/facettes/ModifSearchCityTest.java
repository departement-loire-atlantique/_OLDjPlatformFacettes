package fr.cg44.plugin.facettes;

import static fr.cg44.plugin.facettes.SocleDataInit.createCanton;
import static fr.cg44.plugin.facettes.SocleDataInit.createCity;
import static fr.cg44.plugin.facettes.SocleDataInit.createPlace;
import static fr.cg44.plugin.facettes.SocleDataInit.getResultSearchCity;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jalios.jcms.Publication;

import com.jalios.jcms.test.JcmsTestCase4;

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
	static City city1_1 = createCity("Commune 1.1", 1110);
	static City city1_2 = createCity("Commune 1.2", 1110);
	// Fiche lieux sur commune 1
	static Place place1_1 = createPlace("place 1.1", city1_1);
	static Place place1_2 = createPlace("place 1.2", city1_1);
	// Fiche lieux sur commune 2
	static Place place1_3 = createPlace("place 1.3", city1_2);
	
	/**
	 * TU 2 recherchePubRefCantonCommune
	 */
	static Canton canton2_1 = createCanton("Canton 2.1");
	static Canton canton2_2 = createCanton("Canton 2.2");
	// Commune avec canton
	static City city2_1 = createCity("Commune 2.1", 1210, canton2_1);
	static City city2_2 = createCity("Commune 2.2", 1220, canton2_2);
	// Fiche lieux sur canton 2.1
	static Place place2_1 = createPlace("place 2.1", canton2_1);
	static Place place2_2 = createPlace("place 2.2", canton2_1);
	// Fiche lieux sur canton 2.2
	static Place place2_3 = createPlace("place 2.3", canton2_2);
	
	/**
	 * TU 3 recherchePubRefInverseCantonCommune
	 */
	
	static City city3_1 = createCity("Commune 3.1", 1310);
	static City city3_2 = createCity("Commune 3.2", 1320);	
	static Canton canton3_1 = createCanton("Canton 3.1", city3_1);
	static Canton canton3_2 = createCanton("Canton 3.2", city3_2);	
	static Place place3_1 = createPlace("place 3.1", canton3_1);
	static Place place3_2 = createPlace("place 3.2", canton3_1);
	static Place place3_3 = createPlace("place 3.3", canton3_2);
	
	
	@BeforeClass
	/**
	 * Création des données de test pour chaque test unitaire
	 */
	public static void avantTest() {	
		
		// L'indexation des publications ayant lieu de manière asynchrone dans un thread dédié.
		// la recherche textuelle d'une publication immédiatement après sa création pourrait ne pas renvoyer de résultat.
		// Il est alors nécessaire de faire appel à une temporisation.
		try { Thread.sleep(1000); } catch(Exception ex) { }	
		
		// Modification données test 1
		modifDataRecherchePubDirectCommune();
		// Modification données test 2
		modifDataRecherchePubRefCantonCommune();
		// Modification données test 3
		modifDataRecherchePubRefInverseCantonCommune();
		
		// L'indexation des publications ayant lieu de manière asynchrone dans un thread dédié.
		// la recherche textuelle d'une publication immédiatement après sa création pourrait ne pas renvoyer de résultat.
		// Il est alors nécessaire de faire appel à une temporisation.
		try { Thread.sleep(1000); } catch(Exception ex) { }			
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

	
	/**
	 * Modification données test 2
	 */
	private static void modifDataRecherchePubRefCantonCommune() {
		// Modification du canton de la commune 1.1 (canton 1.1 remplacé par canton 2.2)
		City clone2_1 = (City) city2_1.getUpdateInstance();
		clone2_1.setCanton(canton2_2);
		clone2_1.performUpdate(admin);		
	}
	
	
	/**
	 * Modification données test 3
	 */
	private static void modifDataRecherchePubRefInverseCantonCommune() {
		// Modification du canton 1.1 pour affecter la commune 2.2
		Canton clone3_1 = (Canton) canton3_1.getUpdateInstance();
		clone3_1.setCity(city3_2);
		clone3_1.performUpdate(admin);	
	}
	
			
	@Test
	/**
	 * Test 1 recherche sur commune 1.1
	 * Après modification du code commune de la commune recherchée
	 * Test sur les publications qui référencent directement la commune (champ mono)
	 */
	public void recherchePubDirectCommune1_1() {		
		// Recherche sur commune 1.1
		// city 1.1 est référencée par : place 1.1 et place 1.2
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place1_1);
		resultatTestSet.add(place1_2);  	    
	    assertEquals("Recherche sur commune 3.1 invalide", resultatTestSet, getResultSearchCity(city1_1));	
	}
	
	
	@Test
	/**
	 * Test 2 recherche sur commune 2.1
	 * Après modification du canton de la commune
	 * 
	 * Avant modification :
	 * 
	 * place 2.1 et 2.2 -> canton 2.1
	 * place 2.3 > canton 2.2
	 * 
	 * commune 2.1 -> canton 2.1
	 * commune 2.2 -> canton 2.2
	 * 
	 * Recherche sur commune 2.1 : récupère les places du canton 2.1 -> place 2.1 et 2.2 
	 * 
	 * Après modification :
	 * 
	 * commune 2.1 > canton 2.2
	 * 
	 * Recherche sur commune 2.1 : récupère les places du canton 2.2 -> place 2.3 
	 */
	public void recherchePubRefCantonCommune2_1() {		
		// Recherche sur commune 2.1
		// city 2.1 est référencée par : place 2.3
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place2_3);    	    
	    assertEquals("Recherche sur commune 2.1 invalide", resultatTestSet, getResultSearchCity(city2_1));	
	}

		
	/*-----------------------------------------------------------------------------------/
	 * Test 3
	 * Après modification de la commune dans le canton
	 * 
	 * Avant modification :
	 * 
	 * place 3.1 et 3.2 -> canton 3.1
	 * place 3.3 -> canton 3.2
	 * 
	 * canton 3.1 -> commune 3.1
	 * canton 3.2 -> commune 3.2
	 * 
	 * Recherche sur commune 3.1 : récupère les places du canton 3.1 -> place 3.1 et 3.2
	 * Recherche sur commune 3.2 : récupère les places du canton 3.2 -> place 3.3
	 * 
	 * Après modification :
	 * 
	 * canton 3.1 -> commune 3.2
	 * canton 3.2 -> commune 3.2
	 * 
	 * Recherche sur commune 3.1 : ne récupère aucun canton -> vide
	 * Recherche sur commune 3.2 : récupère les places du canton 3.1 et 3.2 -> place 3.1, place 3.2 et place 3.3
	 *-----------------------------------------------------------------------------------*/
	
	@Test
	/**
	 * Test 3 recherche sur commune 3.1
	 * Après modification de la commune dans le canton
	 * canton 3.1 -> commune 3.2
	 * Recherche sur commune 3.1 : ne récupère aucun canton -> vide
	 */
	public void recherchePubRefInverseCantonCommune3_1() {		
		// Recherche sur commune 3.1
		// city 3.1 est référencée par : vide
		Set<Publication> resultatTestSet = new HashSet<Publication>();    	    
	    assertEquals("Recherche sur commune 3.2 invalide", resultatTestSet, getResultSearchCity(city3_1));
	}
	
	
	@Test
	/**
	 * Test 3 recherche sur commune 3.2
	 * Après modification de la commune dans le canton
	 * canton 3.1 -> commune 3.2
	 * Recherche sur commune 3.2 : récupère les places du canton 3.1 et 3.2 -> place 3.1, place 3.2 et place 3.3
	 */
	public void recherchePubRefInverseCantonCommune3_2() {		
		// Recherche sur commune 3.2
		// city 3.1 est référencée par : place 3.1, place 3.2 et place 3.3
		Set<Publication> resultatTestSet = new HashSet<Publication>();
		resultatTestSet.add(place3_1);
		resultatTestSet.add(place3_2);
		resultatTestSet.add(place3_3);	    	    
	    assertEquals("Recherche sur commune 3.2 invalide", resultatTestSet, getResultSearchCity(city3_2));
	}
	
	
}
