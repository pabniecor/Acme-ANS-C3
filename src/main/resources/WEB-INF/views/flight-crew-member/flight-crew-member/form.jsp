<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="member.flight-crew-member.form.label.employee-code" path="employeeCode"/>
	<acme:input-textbox code="member.flight-crew-member.form.label.phone-number" path="phoneNumber"/>
	<acme:input-moment code="member.flight-crew-member.form.label.languages" path="languageSkills" />
	<acme:input-select code="member.flight-crew-member.form.label.status" path="availabilityStatus" choices="${ statuss}"/>
	<acme:input-textarea code="member.flight-crew-member.form.label.airline" path="airline" />
	<acme:input-textarea code="member.flight-crew-member.form.label.salary" path="salary" />
	<acme:input-textarea code="member.flight-crew-member.form.label.experience" path="yearsOfExperience" />
</acme:form>