<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="member.leg.list.label.flight-number" path="flightNumber" width="20%"/>
	<acme:list-column code="member.leg.list.label.flight" path="flight" width="40%"/>
	<acme:list-column code="member.leg.list.label.arrival-time" path="scheduledArrival" width="20%"/>
	<acme:list-column code="member.leg.list.label.arrival-airport" path="arrivalAirport" width="20%"/>
	<acme:list-payload path="payload"/>
</acme:list>