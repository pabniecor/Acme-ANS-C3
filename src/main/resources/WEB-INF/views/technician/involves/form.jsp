<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	
	<acme:input-select code="technician.involves.form.label.maintenanceRecord" path="maintenanceRecord" choices="${MRs}"/>
	<acme:input-select code="technician.involves.form.label.task" path="task" choices="${tasks}"/>
	
	<jstl:choose>		
 		<jstl:when test="${_command == 'create'}">
 			<acme:input-checkbox code="technician.involves.form.label.confirmation" path="confirmation" />	
 			<acme:submit code="technician.involves.form.button.create.submit" action="/technician/involves/create"/>
 		</jstl:when>
 		<jstl:when test="${_command == 'delete'}">
 		 	<acme:input-checkbox code="technician.involves.form.label.confirmation" path="confirmation" />	
 			<acme:submit code="technician.involves.form.button.delete.submit" action="/technician/involves/delete"/>
 		</jstl:when>		
 	</jstl:choose>
		
</acme:form>