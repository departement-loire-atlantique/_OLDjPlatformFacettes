package fr.cg44.plugin.facettes.queryfilter;

import static com.jalios.jcms.Channel.getChannel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.jalios.jcms.handler.QueryHandler;
import com.jalios.util.Util;

import fr.cg44.plugin.facettes.policyfilter.PublicationFacetedSearchCityEnginePolicyFilter;
import fr.cg44.plugin.facettes.policyfilter.PublicationFacetedSearchDelegationEnginePolicyFilter;

import generated.City;
import generated.Delegation;


/**
 * Filtre pour la facette délégation.
 */
public class DelegationQueryFilter extends LuceneQueryFilter {

	
	@Override
	public QueryHandler doFilter(QueryHandler qh, Map context, HttpServletRequest request) {	
		// Id des Delegation recherchées
		String[] delegationIdParamsTab = request.getParameterValues("delegations");
		// Tableau avec les delegations recherchées
		if(Util.notEmpty(delegationIdParamsTab)) {
			List<Delegation> delegationList =  Arrays.stream(delegationIdParamsTab)
					.map(getChannel()::getPublication)
					.filter(pub -> pub instanceof Delegation)
					.map(pub -> (Delegation) pub)
					.collect(Collectors.toList());						
			addDelegationSearch(qh, request, delegationList);
		}
		return qh;
	}


	/**
	 * Ajoute le filtre sur la commune
	 * Attention la query passe en syntaxe de recherche avancée
	 * @param qh
	 * @param cityData
	 */
	public void addDelegationSearch(QueryHandler qh, HttpServletRequest request, List<Delegation> delegationList) {		
		if(Util.notEmpty(delegationList)) {
			// Passe la query en syntaxe avancée pour accepter les requêtes lucenes
			qh.setMode("advanced");	  
			// Requête pour la recherche sur les delegations (OR entre les delegations)	
			String delegationSearchText = "";
			for(Delegation itDeleg : delegationList) {
				if(Util.notEmpty(delegationSearchText)) {
					delegationSearchText += " OR ";
				}
				delegationSearchText += PublicationFacetedSearchDelegationEnginePolicyFilter.INDEX_FIELD_DELEGATIONS + ":\"" + itDeleg.getZipCode() + "\"";	
				// Ajout les communes de la delegation dans la recherche
				delegationSearchText += addCityDelegationSearch(itDeleg);
			}
			// Requêtes pour incrémenter la recherche des delegations avec les précédants query des autres facettes						  
			addFacetQuery(qh, request, delegationSearchText);
		}	
	}

	
	/**
	 * Retoune une requêtes avec les communes de la délégation (en union - OR)
	 * @param delegation
	 * @return
	 */
	public String addCityDelegationSearch(Delegation delegation) { 
		String citySearchText = "";
		TreeSet<City> citiesRefSet = new TreeSet<>(delegation.getLinkIndexedDataSet(City.class, "delegation"));
		for(City itCity : citiesRefSet) {			
			citySearchText += " OR " + PublicationFacetedSearchCityEnginePolicyFilter.INDEX_FIELD_CITIES + ":\"" + itCity.getCityCode() + "\"";								
		}
		return citySearchText;
	}


}
