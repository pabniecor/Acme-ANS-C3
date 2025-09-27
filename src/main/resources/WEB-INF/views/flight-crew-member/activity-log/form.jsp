<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
<acme:form>
	<%-- <acme:input-select readonly="true" code="member.activity-log.form.label.assignment" path="flightAssignment" choices="${assignments}"/> --%>
	<acme:input-textbox code="member.activity-log.form.label.incident" path="typeOfIncident"/>
	<acme:input-textarea code="member.activity-log.form.label.description" path="description" />
	<acme:input-integer code="member.activity-log.form.label.severity" path="severityLevel" />
	<%-- <acme:input-checkbox readonly="true" code="member.activity-log.form.label.draft" path="draft"/> --%>

	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete') && draft == true}">
			<acme:submit code="member.activity-log.form.button.update" action="/flight-crew-member/activity-log/update"/>
			<acme:submit code="member.activity-log.form.button.publish" action="/flight-crew-member/activity-log/publish"/>
			<acme:submit code="member.activity-log.form.button.delete" action="/flight-crew-member/activity-log/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="member.activity-log.form.button.create" action="/flight-crew-member/activity-log/create?masterId=${masterId}"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>

