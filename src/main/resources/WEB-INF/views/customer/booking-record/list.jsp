<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking-record.list.label.passenger.fullName" path="passenger.fullName" width="30%" />
	<acme:list-column code="customer.booking-record.list.label.passenger.email" path="passenger.email" width="20%" />
	<acme:list-column code="customer.booking-record.list.label.passenger.passportNumber" path="passenger.passportNumber" width="20%" />
	<acme:list-column code="customer.booking-record.list.label.passenger.birthDate" path="passenger.birthDate" width="20%" />
	<acme:list-column code="customer.booking-record.list.label.passenger.draftModePassenger" path="passenger.draftModePassenger" width="10%" />
	<acme:list-payload path="payload"/>	
</acme:list>

<jstl:if test="${showCreate }">
	<acme:button code="customer.booking-record.list.button.create" action="/customer/booking-record/create?bookingId=${bookingId}"/>
</jstl:if>