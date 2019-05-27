package fr.cg44.plugin.facettes.queryfilter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jalios.jcms.Data;
import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.util.Util;

import fr.cg44.plugin.facettes.policyfilter.PublicationFacetedSearchCantonEnginePolicyFilter;
import generated.Canton;


/**
 * Filtre pour la facette canton.
 */
public class CantonQueryFilter extends LuceneQueryFilter {
		
	@Override
	public QueryHandler doFilter(QueryHandler qh, Map context, HttpServletRequest request) {		
		// Récupère le canton 
	    Data cantonData = HttpUtil.getDataParameter(request, "canton");
		// Recherche sur la canton si celui-ci est bien renseigné
	    if(Util.notEmpty(cantonData) && cantonData instanceof Canton) {
	    	// Passe la query en syntaxe avancée pour accepter les requêtes lucenes
	    	qh.setMode("advanced");	    	
	    	Canton canton = (Canton) cantonData;
	    	String cantonCode =  Integer.toString(canton.getCantonCode());
	    	// // Requêtes pour incrémenter la recherche des communes avec les précédants query des autres facettes	
	    	String prevSearchText = "";
	    	if(Util.notEmpty(qh.getText())) {
	    		prevSearchText = qh.getText() + " AND ";
	    	}
	    	// La nouvelle requêtes est settée dans la query
	    	qh.setText(prevSearchText + "(" + PublicationFacetedSearchCantonEnginePolicyFilter.INDEX_FIELD_CANTON + ":\"" + cantonCode +"\")");	    	
	    }	    
		return qh;
	}
		
}
