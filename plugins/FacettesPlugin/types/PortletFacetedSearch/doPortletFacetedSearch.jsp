<%@page import="com.jalios.jcms.handler.QueryHandler"%>
<%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%

%><% PortletFacetedSearch box = (PortletFacetedSearch)portlet; %><%

String searchDisplayId = box.getDisplayPortal() != null ? box.getDisplayPortal().getId() : currentCategory.getId();


%><%@ include file='/jcore/doHeader.jsp' %>

<div>






<%@ include file='/types/PortletSearch/doQueryHandler.jsp' %>
<form action="<%= box.getQueryPortlet() != null ? JcmsUtil.getDisplayUrl() : ResourceHelper.getQuery() %>" method="get" name="search" class="noSingleSubmitButton">


    <jalios:foreach array="<%= box.getOrderedFacets() %>" name="itFacet" type="String">
    
	    <%-- Communes --%>
		<jalios:if predicate='<%= "commune".equalsIgnoreCase(itFacet) %>'>	
			<%-- Commune principale --%>	
			<jalios:field name="commune" label="Commune" value='<%= request.getParameter("commune") %>'>
			   <jalios:control settings="<%= new PublicationSettings().superType(City.class) %>"/>
			</jalios:field>
			<%-- Communes limitrophes --%>
            <jalios:if predicate="<%= box.getFacetBorderingCities() %>">
               <div class="clear">
                  <%
                  boolean borderingCitiesChecked = getBooleanParameter("borderingCities", false);
                  City currentCity = (City) channel.getPublication(request.getParameter("commune"));
                  City[] borderingCities = Util.notEmpty(currentCity) ? currentCity.getClosenessCities() : null;
                  %>
                  <input type="checkbox" id="borderingCities" name="borderingCities" value="true" <%= borderingCitiesChecked ? "checked=\"checked\"" : "" %> />                  
                  <label for="borderingCities">Étendre aux communes limitrophes</label>                  
                  <jalios:if predicate='<%= borderingCitiesChecked %>'>
                    <ul>
                      <jalios:foreach array="<%= borderingCities %>" name="itCityBord" type="City">
                        <li>
                          <% 
                          boolean itIsChecked = false;
                          if(Util.notEmpty(request.getParameterValues("communelimit"))) {
                        	  itIsChecked = Arrays.stream(request.getParameterValues("communelimit")).anyMatch(itCityBord.getId()::equalsIgnoreCase); 
                          }
                          %>
                          <input id="borderingCity<%= itCityBord.getCityCode() %>" type="checkbox" name="communelimit" value="<%= itCityBord.getId() %>" <%= itIsChecked ? "checked" : "" %>/>
                          <label for="borderingCity<%= itCityBord.getCityCode() %>"><%= itCityBord.getTitle() %></label>
                        </li>
                      </jalios:foreach>
                    </ul>
                  </jalios:if>                                  
               </div>
            </jalios:if>			
		</jalios:if>
		
		<%-- Titre --%>
		<jalios:if predicate='<%= "titre".equalsIgnoreCase(itFacet) %>'>
			<%
			QueryHandler qh = new QueryHandler(box.getQuery());
			%>
			<jalios:field name="title" label="Par nom" value='<%= request.getParameter("title")%>'>
			   <jalios:control settings="<%= new PublicationSettings().superType(qh.getTypes()[0]) %>"/>
			</jalios:field>
		</jalios:if>
		
		<%-- Cantons --%>
		<jalios:if predicate='<%= "canton".equalsIgnoreCase(itFacet) %>'>
			<jalios:field name="canton" label="Canton" value='<%= request.getParameter("canton")%>'>
			   <jalios:control settings="<%= new PublicationSettings().superType(Canton.class) %>"/>
			</jalios:field>						
		</jalios:if>
		
		<%-- Catégories liste déroulante --%>
		<jalios:if predicate='<%= "catégories dynamiques".equalsIgnoreCase(itFacet) %>'>
			<jalios:foreach array="<%= box.getDynamicSelectSearchBranches() %>" name="itCat" type="String"> 
			   <jalios:field name="cids" label="<%= channel.getCategory(itCat).getName() %>" value='<%= request.getParameterValues("cids") %>'>
		            <jalios:control settings="<%= new CategorySettings().checkbox().displayRoots(false).root(channel.getCategory(itCat)) %>" />
		        </jalios:field>  
		        <input name="cidBranches" type="text" value='<%= itCat %>'>
			</jalios:foreach>
		</jalios:if>
	
		<%-- Catégories --%>
		<jalios:if predicate='<%= "catégories".equalsIgnoreCase(itFacet) %>'>
			<jalios:foreach collection="<%= box.getSearchBranches(loggedMember) %>" name="itCat" type="Category"> 		   
		        <jalios:field name="cids" label="<%= itCat.getName() %>" value='<%= request.getParameterValues("cids") %>'>
		            <jalios:control settings="<%= new CategorySettings().checkbox().displayRoots(false).root(itCat) %>" />
		        </jalios:field>
		        <input name="cidBranches" type="text" value='<%= itCat.getId() %>'> 	   
			</jalios:foreach>
		</jalios:if>

    </jalios:foreach>
	
	
	
<!-- 	<input type="hidden" name="catMode" value="or" /> -->
	


	<span class="input-group-btn">
	   <button class="btn btn-default" value="true" name="opSearch" type="submit"><jalios:icon src="search"/></button>       
	</span>  
	
	
<%-- 	<input type="hidden" value="true" name="<%= PortalManager.getActionParam(box.getQueryPortlet(),"DynamicQuery") %>" /> --%>
<%--      <input type="hidden" value="<%= box.getQueryPortlet().getId() %>" name="portlet"> --%>
	
	<%= formHandler.getHiddenParams() %>
	<%@ include file='/types/PortletSearch/doHiddenParams.jsp' %> 




</form>

</div>

<div class="clear"></div>