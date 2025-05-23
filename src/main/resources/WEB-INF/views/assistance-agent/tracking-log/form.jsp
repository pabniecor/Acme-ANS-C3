<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${!draftMode}">
	<acme:input-moment code="assistance-agent.tracking-log.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>
	<acme:input-textarea code="assistance-agent.tracking-log.form.label.step" path="step" />
	<acme:input-double code="assistance-agent.tracking-log.form.label.resolutionPercentage" path="resolutionPercentage" readonly="${_command == 'reclaim'}"/>
	<acme:input-select code="assistance-agent.tracking-log.form.label.status" path="status" choices="${trackStatus}" />
	<acme:input-textarea code="assistance-agent.tracking-log.form.label.resolution" path="resolution" />
	<acme:input-checkbox code="assistance-agent.tracking-log.form.label.draftMode" path="draftMode" readonly="true"/>
	<acme:input-select code="assistance-agent.tracking-log.form.label.claim" path="claim" choices="${claims}" readonly="${_command == 'reclaim'}" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:input-checkbox code="assistance-agent.tracking-log.form.label.confirmation" path="confirmation"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.update" action="/assistance-agent/tracking-log/update"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.publish" action="/assistance-agent/tracking-log/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'show' && reclaim == true}">
			<acme:button code="assistance-agent.tracking-log.show.button.reclaim" action="/assistance-agent/tracking-log/reclaim?masterId=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'reclaim'}">
			<acme:input-checkbox code="assistance-agent.tracking-log.form.label.confirmation" path="confirmation"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.reclaim" action="/assistance-agent/tracking-log/reclaim?masterId=${masterId}"/>
		</jstl:when>	
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="assistance-agent.tracking-log.form.label.confirmation" path="confirmation"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.create" action="/assistance-agent/tracking-log/create"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>