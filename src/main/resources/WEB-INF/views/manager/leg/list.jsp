<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.leg.list.label.flightNumber" path="flightNumber" width="20%" />
	<acme:list-column code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="10%" />
	<acme:list-column code="manager.leg.list.label.scheduledArrival" path="scheduledArrival" width="10%" />
	<acme:list-column code="manager.leg.list.label.status" path="status" width="20%" />
	<acme:list-column code="manager.leg.list.label.draftMode" path="draftMode" width="10%" />
	<acme:list-payload path="payload"/>	
</acme:list>

<acme:button code="manager.leg.list.button.create" action="/manager/leg/create?masterId=${masterId}"/>