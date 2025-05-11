<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.label.fullName" path="fullName" width="30%" />
	<acme:list-column code="customer.passenger.list.label.email" path="email" width="20%" />
	<acme:list-column code="customer.passenger.list.label.passportNumber" path="passportNumber" width="20%" />
	<acme:list-column code="customer.passenger.list.label.birthDate" path="birthDate" width="20%" />
	<acme:list-column code="customer.passenger.list.label.draftModePassenger" path="draftModePassenger" width="10%" />
	<acme:list-payload path="payload"/>	
</acme:list>

<acme:button code="customer.passenger.form.button.create" action="/customer/passenger/create"/>

