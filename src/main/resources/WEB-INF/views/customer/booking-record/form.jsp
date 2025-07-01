<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.booking-record.form.label.passenger" path="passenger" choices="${passengers}"/>
			<acme:submit code="customer.booking-record.form.button.create" action="/customer/booking-record/create?bookingId=${booking.id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'show'}">
			<acme:input-textbox code="customer.booking-record.form.label.passenger" path="passengerName" readonly="true"/>
			<jstl:if test="${booking.draftMode == true}">
				<acme:submit code="customer.booking-record.form.button.delete" action="/customer/booking-record/delete"/>
			</jstl:if>
		</jstl:when>
	</jstl:choose>
</acme:form>