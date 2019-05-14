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


	
	<jalios:field name="commune" label="Commune" value='<%= request.getParameter("commune")%>'>
	   <jalios:control settings="<%= new PublicationSettings().superType(City.class) %>"/>
	</jalios:field>
	
	
	<%
	QueryHandler qh = new QueryHandler(box.getQuery());
	%>
	<jalios:field name="title" label="Par nom" value='<%= request.getParameter("title")%>'>
	   <jalios:control settings="<%= new PublicationSettings().superType(qh.getTypes()[0]) %>"/>
	</jalios:field>
	
	
	<jalios:field name="canton" label="Canton" value='<%= request.getParameter("canton")%>'>
	   <jalios:control settings="<%= new PublicationSettings().superType(Canton.class) %>"/>
	</jalios:field>
	
	
	<jalios:foreach array="<%= box.getDynamicSelectSearchBranches() %>" name="itCat" type="String"> 
	   <jalios:field name="cids" label="<%= channel.getCategory(itCat).getName() %>" value='<%= request.getParameterValues("cids") %>'>
            <jalios:control settings="<%= new CategorySettings().checkbox().displayRoots(false).root(channel.getCategory(itCat)) %>" />
        </jalios:field>  
        <input name="cidBranches" type="text" value='<%= itCat %>'>
	</jalios:foreach>

	
	<jalios:foreach collection="<%= box.getSearchBranches(loggedMember) %>" name="itCat" type="Category"> 		   
        <jalios:field name="cids" label="<%= itCat.getName() %>" value='<%= request.getParameterValues("cids") %>'>
            <jalios:control settings="<%= new CategorySettings().checkbox().displayRoots(false).root(itCat) %>" />
        </jalios:field>	   	   
	</jalios:foreach>


	
	<input type="hidden" name="catMode" value="or" />
	
	
	<jalios:foreach collection="<%= box.getSearchBranches(loggedMember) %>" name="itCat" type="Category">
	   <input name="cidBranches" type="text" value='<%= itCat.getId() %>'>
	</jalios:foreach>
	
	
	
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