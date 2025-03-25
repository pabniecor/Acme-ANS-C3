<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.aircraft.form.label.model" path="model" />
	<acme:input-textbox code="administrator.aircraft.form.label.registrationNumber" path="registrationNumber" />
	<acme:input-integer code="administrator.aircraft.form.label.capacity" path="capacity" />
	<acme:input-integer code="administrator.aircraft.form.label.cargoWeight" path="cargoWeight" />
	<acme:input-select code="administrator.aircraft.form.label.status" path="status" choices="${status}" />
	<acme:input-textbox code="administrator.aircraft.form.label.details" path="details" />
	<acme:input-select code="administrator.aircraft.form.label.airline" path="airline" choices="${airlines}" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete')}">
			<acme:submit code="administrator.aircraft.form.button.update" action="/administrator/aircraft/update"/>
			<acme:submit code="administrator.aircraft.form.button.delete" action="/administrator/aircraft/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.aircraft.form.button.create" action="/administrator/aircraft/create"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>