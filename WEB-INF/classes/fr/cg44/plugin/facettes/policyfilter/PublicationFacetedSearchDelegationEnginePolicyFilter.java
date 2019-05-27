package fr.cg44.plugin.facettes.policyfilter;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

import com.jalios.jcms.Publication;
import com.jalios.jcms.policy.BasicLuceneSearchEnginePolicyFilter;
import com.jalios.util.Util;

import generated.Delegation;


/**
 * Indexe le code canton dans les publication qui référence des cantons
 */
public class PublicationFacetedSearchDelegationEnginePolicyFilter extends BasicLuceneSearchEnginePolicyFilter {

	private static final Logger LOGGER = Logger.getLogger(PublicationFacetedSearchDelegationEnginePolicyFilter.class);
		
	public static final String INDEX_FIELD_DELEGATIONS = "facet_delegations";
	
	@Override
	public void filterPublicationDocument(Document doc, Publication publication, String lang) {   							
		// Récupère le champ "delegation" du type de contenu pour l'indexer si celui-ci est présent
		try {
			Delegation[] delegationPubTab = (Delegation[]) publication.getFieldValue("delegations");
			if(Util.notEmpty(delegationPubTab)) {
				for(Delegation itDeleg : delegationPubTab) {
					indexCantonZipCode(doc, itDeleg);
				}
			}
		} catch (NoSuchFieldException e) {
			LOGGER.trace("Le contenu n'a pas de référence à une délégation à indexer", e);
		}					
	}

	
	/**
	 * Indexe le code delegation sur la publication
	 * @param doc
	 * @param city
	 */
	private void indexCantonZipCode(Document doc, Delegation delegation){
		if(Util.notEmpty(delegation)) {
			String delegationZipCode = delegation.getZipCode();
			Field cityField = new StringField(INDEX_FIELD_DELEGATIONS, delegationZipCode, Field.Store.NO);
			doc.add(cityField);	
		}
	}
	
}