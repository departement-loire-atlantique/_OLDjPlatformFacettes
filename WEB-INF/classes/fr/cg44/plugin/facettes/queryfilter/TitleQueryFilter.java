package fr.cg44.plugin.facettes.queryfilter;

import static com.jalios.jcms.Channel.getChannel;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.jalios.jcms.Data;
import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.QueryFilter;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.util.Util;

import fr.cg44.plugin.facettes.policyfilter.PublicationFacetedSearchCityEnginePolicyFilter;
import generated.City;


/**
 * Filtre pour la facette commune.
 */
public class TitleQueryFilter extends QueryFilter {
	

	public QueryHandler filterQueryHandler(QueryHandler qh, Map context) {
		
		HttpServletRequest request = getChannel().getCurrentServletRequest();
		
		if(Util.isEmpty(request)) {
			return qh;
		}
		
	    Data cityData = HttpUtil.getDataParameter(request, "title");
		
	    if(Util.notEmpty(cityData) && cityData instanceof City) {
	    	
	    	qh.setMode("advanced");	    	
	    	City city = (City) cityData;
	    	String cityCode =  Integer.toString(city.getCityCode());
	    	
	    	String prevSearchText = "";
	    	if(Util.notEmpty(qh.getText())) {
	    		prevSearchText = qh.getText() + " AND ";
	    	}
	    	
	    	qh.setText(prevSearchText + "(" + PublicationFacetedSearchCityEnginePolicyFilter.INDEX_FIELD_CITY + ":\"" + cityCode +"\")");	    	

	    }
		    
		return qh;
	}
	
	
	
}
