<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="technician.maintenanceRecord.form.label.momentDone" path="momentDone" />
	<acme:input-select code="technician.maintenanceRecord.form.label.maintenanceStatus" path="maintenanceStatus" choices="${status}" />
	<acme:input-moment code="technician.maintenanceRecord.form.label.nextInspection" path="nextInspection" />
	<acme:input-money code="technician.maintenanceRecord.form.label.estimatedCost" path="estimatedCost" />
	<acme:input-textarea code="technician.maintenanceRecord.form.label.notes" path="notes" />
	<acme:input-select code="technician.maintenanceRecord.form.label.aircraft" path="aircraft" choices="${aircrafts}" />
	<acme:input-select code="technician.maintenanceRecord.form.label.technician" path="technician" choices="${technicians}" />
	<acme:input-textarea code="technician.maintenanceRecord.form.label.tasks" path="tasks" />
	
	<jstl:if test="${!readOnly}">
		<acme:input-checkbox code="technician.maintenanceRecord.form.label.confirmation" path="confirmation" />
		
		<jstl:choose>
	 		<jstl:when test="${acme:anyOf(_command, 'show|update|delete')}">
	 			<acme:submit code="technician.maintenanceRecord.form.button.update" action="/technician/maintenance-record/update"/>
	 			<acme:submit code="technician.maintenanceRecord.form.button.delete" action="/technician/maintenance-record/delete"/>
	 		</jstl:when>
	 		<jstl:when test="${_command == 'create'}">
	 			<acme:submit code="technician.maintenanceRecord.form.button.create" action="/technician/maintenance-record/create"/>
	 		</jstl:when>		
	 	</jstl:choose>
 	</jstl:if>
	
</acme:form>