<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="member.flight-assignment.list.label.status" path="currentStatus" width="20%"/>
	<acme:list-column code="member.flight-assignment.list.label.duty" path="duty" width="20%"/>
	<acme:list-column code="member.flight-assignment.list.label.moment" path="moment" width="40%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="member.flight-assignment.list.button.create" action="/flight-crew-member/flight-assignment/create?masterId=${masterId}"/>