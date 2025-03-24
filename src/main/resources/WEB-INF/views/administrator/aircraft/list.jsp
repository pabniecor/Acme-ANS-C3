<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.aircraft.list.label.model" path="model" width="20%"/>
	<acme:list-column code="administrator.aircraft.list.label.registrationNumber" path="registrationNumber" width="20%"/>
	<acme:list-column code="administrator.aircraft.list.label.capacity" path="capacity" width="10%"/>
	<acme:list-column code="administrator.aircraft.list.label.cargoWeight" path="cargoWeight" width="10%"/>
	<acme:list-column code="administrator.aircraft.list.label.status" path="status" width="10%"/>
	<acme:list-column code="administrator.aircraft.list.label.details" path="details" width="20%"/>
	<acme:list-column code="administrator.aircraft.list.label.airline" path="airline" width="10%"/>
	<acme:list-payload path="payload"/>	
</acme:list>

<acme:button code="administrator.aircraft.list.button.create" action="/administrator/aircraft/create"/>