package fr.cg44.plugin.facettes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.jalios.jcms.Category;
import com.jalios.jcms.JcmsUtil;

import static com.jalios.jcms.Channel.getChannel;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.util.ServletUtil;
import com.jalios.util.Util;

/**
 * Classe utilitaire pour la recherche à facettes sur les catégories
 * La facette catégorie ne possède pas de query filter pour ses filtres,
 * la jsp doQueryHandlers.jspf du coeur est modifiée avec les méthodes de cette classe
 */
public class CategoryFacetUtil {


	/**
	 * A partir d'une requete récupère le query handler et des cid de branche 
	 * créé autant de requête de recherche que de branches
	 * @param request la requête qui doit contenir la recherche et les branches de recherche 
	 * @return autant de requête de recherche que de branche
	 */
	public static String[] getFacetCategoryQuery(HttpServletRequest request) {		
		// Récupère la query de la recherche pour modifier son paramètre cids
		QueryHandler qh = new QueryHandler(ServletUtil.getQueryString(request, false));     	
		// Récupère les branches de catégories de la recherche à facettes
		String[] cidBranches = request.getParameterValues("cidBranches");
		return getFacetCategoryQuery(qh, cidBranches);
	}


	/**
	 * A partir d'un query handler et des cid de branche 
	 * créé autant de requête de recherche que de branches
	 * @param qh La recherche de base
	 * @param cidBranches Les branches de recherche 
	 * @return
	 */
	public static String[] getFacetCategoryQuery(QueryHandler qh, String[] cidBranches) {
		// Création d'autant de query qu'il y a de branche de catégories dans la recherche à facettes plus deux
		String[] queries = new String[cidBranches.length + 2];	
    	// Récupère la liste cids de la query
    	List<String> cidsParamList = Util.notEmpty(qh.getCids()) ? Arrays.asList(qh.getCids()) : new ArrayList<String>(); 	
    	List<Category> cidsParamCatList = JcmsUtil.idCollectionToDataList(cidsParamList, Category.class);
    	// Itération sur chacune des branches de la recherche.
    	// Chaque branche à sa propre query et cette query ne doit avoir que les cids enfants de sa branche
    	for(int i = 0 ; i < cidBranches.length ; i++){
    		
			// Récupère la query de la recherche pour modifier son paramètre cids
			QueryHandler itQh = new QueryHandler(qh);   		   		
			
			// Si un enfant d'une catégorie est sélectionné alors
			// la catégorie parente est retirée des cids						
			List<Category> cidsParamParentCatList = cidsParamCatList.stream().map(Category::getParent).collect(Collectors.toList());
			List<String> cidsFilterCatIdList = cidsParamCatList.stream().filter(e -> !cidsParamParentCatList.contains(e) ).map(Category::getId).collect(Collectors.toList());
			
			// Récupère les catégories enfants de cette branche de catégorie (permettra de filtrer seulement sur les cids de cette branche)
			String itCidBranche = cidBranches[i];
			Category itCatBranche = getChannel().getCategory(itCidBranche);			
			Set<Category> itCatDescendantBrancheSet = itCatBranche.getDescendantSet();          
			List<String> itCatDescendantBrancheIdSet = JcmsUtil.dataCollectionToIdList(itCatDescendantBrancheSet);  
	
			// Ne retient que les cids de la branche courante sans les parents d'une catégorie déjà sélectionnée		    		
			itCatDescendantBrancheIdSet.retainAll(cidsFilterCatIdList);  
			// Si aucun cids de branche de retenu alors garde les cids par défaut de la recherche à facettes
			if(Util.notEmpty(itCatDescendantBrancheIdSet)) {
				itQh.setCids(itCatDescendantBrancheIdSet.toArray(new String[itCatDescendantBrancheIdSet.size()]) ); 
			}
			// La query est ajoutée au tableau des query pour être éxécutée
			queries[i] =  itQh.getQueryString();
		}
    		  
    	// Créaction d'un requete avec les catégories séléctionnées dans l'éditeur de requêtes depuis la portlet recherche
		queries[cidBranches.length] = getCatSearch(qh, cidBranches);

		// Ajout des catégories de branches
		QueryHandler qhBranches = new QueryHandler(qh);   
		qhBranches.setCids(cidBranches);
		queries[cidBranches.length + 1] =  qhBranches.getQueryString(); 

		return queries;
	}
	

	/**
	 * Créaction d'un requete avec les catégories qui ne sont pas dans les branches.
	 * Typiquement les catégories séléctionnées dans l'éditeur de requêtes depuis la portlet recherche
	 * @param qh Le query handler de la recherche
	 * @param cidBranches Les branches de la recherche à facettes
	 * @return Créaction d'un requete avec les catégories séléctionnées dans l'éditeur de requêtes depuis la portlet recherche.
	 */
	private static String getCatSearch(QueryHandler qh, String[] cidBranches) {
		QueryHandler myQh = new QueryHandler(qh);  
		Set<String> cidsQuery = new HashSet<>(Arrays.asList(myQh.getCids())); 
    	Set<String> cidsWitchBranche = new HashSet<String>(); 	
    	// Retire les id de catégorie de chaque branche et de leurs descendants
    	for(int i = 0 ; i < cidBranches.length ; i++){
    		String itCidBranche = cidBranches[i];
    		Category itCatBranche = getChannel().getCategory(itCidBranche);    		
    		Set<Category> itCatDescendantBrancheSet = itCatBranche.getDescendantSet();
    		List<String> itCatDescendantBrancheIdSet = JcmsUtil.dataCollectionToIdList(itCatDescendantBrancheSet);   				
    		cidsWitchBranche.add(itCidBranche);
			cidsWitchBranche.addAll(itCatDescendantBrancheIdSet);
    	}       	
		cidsQuery.removeAll(cidsWitchBranche);
		myQh.setCids(cidsQuery.toArray(new String[cidsQuery.size()]) ); 
		return myQh.getQueryString();
	}

}
