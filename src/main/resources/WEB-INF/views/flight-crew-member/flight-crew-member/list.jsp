<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list navigable="false">
	<acme:list-column code="member.flight-crew-member.list.label.employee-code" path="employeeCode" width="20%"/>
	<acme:list-column code="member.flight-crew-member.list.label.status" path="availabilityStatus" width="40%"/>
	<acme:list-column code="member.flight-crew-member.list.label.airline" path="airline" width="40%"/>
	<acme:list-payload path="payload"/>
</acme:list>