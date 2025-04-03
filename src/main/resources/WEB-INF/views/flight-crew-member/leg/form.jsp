<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="member.leg.form.label.flight-number" path="flightNumber"/>
	<acme:input-moment code="member.leg.form.label.scheduled-departure" path="scheduledDeparture"/>
	<acme:input-moment code="member.leg.form.label.scheduled-arrival" path="scheduledArrival" />
	<acme:input-select code="member.leg.form.label.status" path="status" choices="${ statuss }"/>
	<acme:input-textbox code="member.leg.form.label.flight" path="flight" />
	<acme:input-textbox code="member.leg.form.label.departure-airport" path="departureAirport" />
	<acme:input-textbox code="member.leg.form.label.arrival-airport" path="arrivalAirport" />
	<acme:input-textbox code="member.leg.form.label.aircraft" path="aircraft" />
</acme:form>