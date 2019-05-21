package fr.cg44.plugin.facettes.queryfilter;

import static com.jalios.jcms.Channel.getChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.QueryFilter;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.util.Util;

import fr.cg44.plugin.facettes.policyfilter.PublicationFacetedSearchCityEnginePolicyFilter;
import generated.City;


/**
 * Filtre pour la facette commune.
 */
public class CityQueryFilter extends QueryFilter {

	public QueryHandler filterQueryHandler(QueryHandler qh, Map context) {
		HttpServletRequest request = getChannel().getCurrentServletRequest();	
		if(Util.isEmpty(request)) {
			return qh;
		}		
		List<City> citiesSearchList = new ArrayList<City>();
		// Commune principale
		City cityData = HttpUtil.getDataParameter(request, "commune", City.class);
		citiesSearchList.add(cityData);	
		// Commune limitrophes
		String[] citiesBordIdTab = request.getParameterValues("communelimit");
		if(Util.notEmpty(citiesBordIdTab)) {
			List<City> citiesBordList =  Arrays.stream(citiesBordIdTab)
					.map(getChannel()::getPublication)
					.filter(pub -> pub instanceof City)
					.map(pub -> (City) pub)
					.collect(Collectors.toList());
			citiesSearchList.addAll(citiesBordList);
		}			
		// Ajoute la commune principale et les communes limitrophes à la query
		City[] citiesSearchArray = citiesSearchList.toArray(new City[citiesSearchList.size()]);		
		addCitySearch(qh, citiesSearchArray);
		return qh;
	}


	/**
	 * Ajoute le filtre sur la commune
	 * Attention la query passe en syntaxe de recherche avancée
	 * @param qh
	 * @param cityData
	 */
	public static void addCitySearch(QueryHandler qh, City... cityData) {		
		if(Util.notEmpty(cityData)) {
			// Passe la query en syntaxe avancée pour accepter les requêtes lucenes
			qh.setMode("advanced");	  
			// Requête pour la recherche sur les communes (OR entre les communes)	
			String citySearchText = "";
			for(City itCity : cityData) {
				if(Util.notEmpty(citySearchText)) {
					citySearchText += " OR ";
				}
				citySearchText += PublicationFacetedSearchCityEnginePolicyFilter.INDEX_FIELD_CITIES + ":\"" + itCity.getCityCode() + "\"";								
			}
			// Requêtes pour incrémenter la recherche des communes avec les précédants query des autres facettes						
			String prevSearchText = "";
			if(Util.notEmpty(qh.getText())) {
				prevSearchText = qh.getText() + " AND ";
			}	
			// La nouvelle requêtes est settée dans la query
			qh.setText(prevSearchText + "(" + citySearchText +")");	    	
		}	
	}



}
