<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="technician.maintenanceRecord.form.label.momentDone" path="momentDone" />
	<acme:input-select code="technician.maintenanceRecord.form.label.maintenanceStatus" path="maintenanceStatus" choices="${status}" />
	<acme:input-moment code="technician.maintenanceRecord.form.label.nextInspection" path="nextInspection" />
	<acme:input-integer code="technician.maintenanceRecord.form.label.estimatedCost" path="estimatedCost" />
	<acme:input-textarea code="technician.maintenanceRecord.form.label.notes" path="notes" />
	<acme:input-textbox code="technician.maintenanceRecord.form.label.aircraft" path="aircraft" />
	<acme:input-textbox code="technician.maintenanceRecord.form.label.technician" path="technician" />
	<acme:input-textarea code="technician.maintenanceRecord.form.label.tasks" path="tasks" />
	
</acme:form>