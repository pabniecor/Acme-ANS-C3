<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.leg.form.label.flightNumber" path="flightNumber" />
	<acme:input-moment code="manager.leg.form.label.scheduledDeparture" path="scheduledDeparture" />
	<acme:input-moment code="manager.leg.form.label.scheduledArrival" path="scheduledArrival" />
	<acme:input-select code="manager.leg.form.label.status" path="status" choices="${status}" />
	<acme:input-select code="manager.leg.form.label.flight" path="flight" choices="${flights}" />
	<acme:input-select code="manager.leg.form.label.departureAirport" path="departureAirport" choices="${departureAirports}" />
	<acme:input-select code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}" />
	<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${aircrafts}" />
	<acme:input-integer code="manager.leg.form.label.sequenceOrder" path="sequenceOrder" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|disable|publish') && published == false}">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create?masterId=${masterId}"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>