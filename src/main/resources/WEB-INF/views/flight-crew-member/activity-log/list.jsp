<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="member.activity-log.list.label.flight-assignment" path="flightAssignment" width="30%"/>
	<acme:list-column code="member.activity-log.list.label.moment" path="registrationMoment" width="30%"/>
	<acme:list-column code="member.activity-log.list.label.incident" path="typeOfIncident" width="20%"/>
	<acme:list-column code="member.activity-log.list.label.severity" path="severityLevel" width="20%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="member.activity-log.list.button.create" action="/flight-crew-member/activity-log/create?masterId=${masterId}"/>

