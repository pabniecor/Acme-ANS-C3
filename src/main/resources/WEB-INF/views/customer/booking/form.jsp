<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.booking.form.label.locatorCode" path= "locatorCode" />
	<acme:input-moment code="customer.booking.form.label.purchaseMoment" path="purchaseMoment" />
	<acme:input-select code="customer.booking.form.label.travelClass" path="travelClass" choices="${travelClass}" />
	<acme:input-money code="customer.booking.form.label.price" path="price" />
	<acme:input-textbox code="customer.booking.form.label.lastCardNibble" path="lastCardNibble" />
	<acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flights}"/>
	<acme:input-select code="customer.booking.form.label.customer" path="customer" choices="${customers}" />
	
	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="customer.booking.form.button.passengers" action="/customer/passenger/list?bookingId=${id}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">
			<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
			<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
			<acme:button code="customer.booking.form.button.passengers" action="/customer/passenger/list?bookingId=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
		</jstl:when>		
	</jstl:choose>
	
</acme:form>