package fr.cg44.plugin.facettes.channellistener;

import static com.jalios.jcms.Channel.getChannel;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.jalios.jcms.Data;
import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.QueryFilter;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.jcms.plugin.Plugin;
import com.jalios.util.Util;

import fr.cg44.plugin.socle.indexation.policyfilter.PublicationFacetedSearchCantonEnginePolicyFilter;
import generated.Canton;


/**
 * Filtre pour la facette canton.
 */
public class CantonQueryFilter extends QueryFilter {
	
	private static final Logger LOGGER = Logger.getLogger(CantonQueryFilter.class);

	public boolean init(Plugin plugin) {	 
		LOGGER.debug("CantonQueryFilter is enabled");
		return true;
	}
	
	
	public QueryHandler filterQueryHandler(QueryHandler qh, Map context) {
		
		HttpServletRequest request = getChannel().getCurrentServletRequest();
		if(Util.isEmpty(request)) {
			return qh;
		}
		
		
	    Data cantonData = HttpUtil.getDataParameter(request, "canton");
		
	    if(Util.notEmpty(cantonData) && cantonData instanceof Canton) {
	    	
	    	qh.setMode("advanced");	    	
	    	Canton canton = (Canton) cantonData;
	    	String cantonCode =  Integer.toString(canton.getCantonCode());
	    	
	    	String prevSearchText = "";
	    	if(Util.notEmpty(qh.getText())) {
	    		prevSearchText = qh.getText() + " AND ";
	    	}
	    	
	    	qh.setText(prevSearchText + "(" + PublicationFacetedSearchCantonEnginePolicyFilter.INDEX_FIELD_CANTON + ":\"" + cantonCode +"\")");	    	

	    }
		    
		return qh;
	}
	
	
	
}
