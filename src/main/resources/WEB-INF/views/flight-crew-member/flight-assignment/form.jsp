<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="member.flight-assignment.form.label.leg" path="leg" choices="${legs}"/>
	<acme:input-select code="member.flight-assignment.form.label.duty" path="duty" choices="${duties}"/>
	<acme:input-select code="member.flight-assignment.form.label.status" path="currentStatus" choices="${status}" />
	<acme:input-textarea code="member.flight-assignment.form.label.remarks" path="remarks" />
	
	<jstl:if test="${acme:anyOf(_command, 'show|update|publish|delete')&&draft == false}">
			<acme:button code="member.flight-assignment.form.button.legs" action="/flight-crew-member/leg/list?masterId=${id}" />
			<acme:button code="member.flight-assignment.form.button.fcm" action="/flight-crew-member/flight-crew-member/list?masterId=${id}" />
			<acme:button code="member.flight-assignment.form.button.als" action="/flight-crew-member/activity-log/list?masterId=${id}" />
	</jstl:if>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete') && draft == true}">
			<acme:submit code="member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
			<acme:submit code="member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>