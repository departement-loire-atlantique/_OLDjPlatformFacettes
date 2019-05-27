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
 * Classe mère pour les filtres
 */
public abstract class LuceneQueryFilter extends QueryFilter {

	
    public QueryHandler filterQueryHandler(QueryHandler qh, Map context) {
        HttpServletRequest request = getChannel().getCurrentServletRequest();   
        if(Util.isEmpty(request)) {
            return qh;
        }            
        return doFilter(qh, context, request);
    }
    
    
    public abstract QueryHandler doFilter(QueryHandler qh, Map context, HttpServletRequest request);

}
