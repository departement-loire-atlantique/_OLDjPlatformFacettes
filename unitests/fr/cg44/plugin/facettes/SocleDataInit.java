package fr.cg44.plugin.facettes;

import static com.jalios.jcms.Channel.getChannel;

import java.util.Set;

import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.jcms.handler.QueryHandler;

import fr.cg44.plugin.facettes.queryfilter.CityQueryFilter;
import generated.Canton;
import generated.City;
import generated.Place;

/**
 * Permet la création de données pour les tests unitaires
 *
 */
public class SocleDataInit   {

	private static final Member ADMIN = getChannel().getDefaultAdmin();
	
	/**
	 * Lance la recherche sur une commune
	 * Retoune les fiches lieux corrspondantes à la recherche
	 * @return
	 */
	public static Set<Publication> getResultSearchCity(City city){
		QueryHandler qh = new QueryHandler();
		qh.setTypes("Place");
		CityQueryFilter.addCitySearch(qh, city);
		return qh.getResultSet();
	}
	
	/**
	 * Création d'une commune
	 * @param name
	 * @param code
	 * @param canton
	 * @return
	 */
	public static City createCity(String name, int code, Canton canton) {
		City city = new City();
		city.setTitle(name);
		city.setCityCode(code);
		city.setCanton(canton);
		city.setAuthor(ADMIN);
		city.performCreate(ADMIN);	
		return city;
	}

	public static City createCity(String name, int code) {
		return createCity(name, code, null);
	}
	
	/**
	 * Création d'un canton
	 * @param name
	 * @param code
	 * @param city
	 * @return
	 */
	public static Canton createCanton(String name, int code, City city) {
		Canton canton = new Canton();
		canton.setTitle(name);
		canton.setCantonCode(code);
		canton.setCity(city);
		canton.setAuthor(ADMIN);
		canton.performCreate(ADMIN);	
		return canton;
	}
	
	public static Canton createCanton(String name, City city) {
		return createCanton(name, 0, city);
	}
	
	public static Canton createCanton(String name) {
		return createCanton(name, 0, null);
	}

	/**
	 * Création d'une fiche lieux
	 * @param name
	 * @param city
	 * @param canton
	 * @return
	 */
	public static Place createPlace(String name, City city, Canton canton) {
		Place place = new Place();
		place.setTitle(name);
		place.setCity(city);
		place.setCanton(canton);
		place.setAuthor(ADMIN);
		place.performCreate(ADMIN);
		return place;
	}
	
	public static Place createPlace(String name, City city) {
		return createPlace(name, city, null);
	}
	
	public static Place createPlace(String name, Canton canton) {
		return createPlace(name, null, canton);
	}

}