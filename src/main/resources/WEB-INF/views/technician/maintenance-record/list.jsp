<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.maintenanceRecord.list.label.momentDone" path="momentDone" width="25%"/>
	<acme:list-column code="technician.maintenanceRecord.list.label.maintenanceStatus" path="maintenanceStatus" width="25%"/>
	<acme:list-column code="technician.maintenanceRecord.list.label.nextInspection" path="nextInspection" width="25%"/>
	<acme:list-column code="technician.maintenanceRecord.list.label.estimatedCost" path="estimatedCost" width="25%"/>
	<acme:list-payload path="payload"/>	
</acme:list>
 
 
<jstl:if test="${_command == 'list'}">
	<acme:button code="technician.maintenanceRecord.list.button.create" action="/technician/maintenance-record/create"/>
	<acme:button code="technician.involves.form.button.create" action="/technician/involves/create"/>
	<acme:button code="technician.involves.form.button.delete" action="/technician/involves/delete"/>
</jstl:if>