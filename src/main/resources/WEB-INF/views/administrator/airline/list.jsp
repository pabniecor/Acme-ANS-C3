<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.airline.list.label.name" path="name" width="25%"/>
	<acme:list-column code="administrator.airline.list.label.iataCode" path="iataCode" width="25%"/>
	<acme:list-column code="administrator.airline.list.label.website" path="website" width="20%"/>
	<acme:list-column code="administrator.airline.list.label.airlineType" path="airlineType" width="30%" />
	<acme:list-payload path="payload"/>	
	
</acme:list>

<acme:button code="administrator.airline.list.button.create" action="/administrator/airline/create"/>