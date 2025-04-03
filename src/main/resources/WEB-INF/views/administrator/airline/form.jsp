<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.airline.form.label.name" path="name" />
	<acme:input-textbox code="administrator.airline.form.label.iataCode" path="iataCode" />
	<acme:input-textbox code="administrator.airline.form.label.website" path="website" />
	<acme:input-select code="administrator.airline.form.label.airlineType" path="airlineType" choices="${airlineType}" />
	<acme:input-moment code="administrator.airline.form.label.foundationMoment" path="foundationMoment"/>
	<acme:input-email code="administrator.airline.form.label.email" path="email"/>
	<acme:input-textbox code="administrator.airline.form.label.phoneNumber" path="phoneNumber"/>
	
	<jstl:if test="${!readOnly}">
		<acme:input-checkbox code="administrator.airline.form.label.confirmation" path="confirmation" />
		
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|disable')}">
			<acme:submit code="administrator.airline.form.button.update" action="/administrator/airline/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.airline.form.button.create" action="/administrator/airline/create"/>
		</jstl:when>		
	</jstl:choose>
	</jstl:if>
	
</acme:form>