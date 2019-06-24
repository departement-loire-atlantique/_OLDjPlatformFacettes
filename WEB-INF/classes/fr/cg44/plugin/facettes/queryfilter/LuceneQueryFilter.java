package fr.cg44.plugin.facettes.queryfilter;

import static com.jalios.jcms.Channel.getChannel;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.QueryFilter;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.util.Util;


/**
 * Classe mère pour les filtres
 */
public abstract class LuceneQueryFilter extends QueryFilter {

	/**
	 * Ne pas surcharger (Surcharger la méthode doFilter)
	 */
    public QueryHandler filterQueryHandler(QueryHandler qh, Map context) {
        HttpServletRequest request = getChannel().getCurrentServletRequest();   
        if(Util.isEmpty(request)) {
            return qh;
        }            
        return doFilter(qh, context, request);
    }
    
    
    public abstract QueryHandler doFilter(QueryHandler qh, Map context, HttpServletRequest request);
    
    
    /**
     * Ajoute la requete lucene à la query en intersection (AND) ou en union (OR) suivant le paramètre de requete 'facetOperator'
     * @param qh
     * @param request
     * @return
     */
    public QueryHandler addFacetQuery(QueryHandler qh, HttpServletRequest request, String query) {
    	// Opérateur de la requete entre les facettes
    	boolean isfacetAndOperator = HttpUtil.getBooleanParameter(request, "facetOperator", true);
    	String operator = isfacetAndOperator ? " AND " : " OR ";
    	// Requêtes pour incrémenter la recherche des communes avec les précédants query des autres facettes	
    	String prevSearchText = "";
    	if(Util.notEmpty(qh.getText())) {
    		prevSearchText = qh.getText() + operator;
    	}
    	// La nouvelle requêtes est settée dans la query
    	qh.setText(prevSearchText + "(" + query +")");	  
    	return qh;
    }
    
    
}
