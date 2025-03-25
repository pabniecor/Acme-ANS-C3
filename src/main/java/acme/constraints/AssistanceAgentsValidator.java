
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.AssistanceAgents;
import acme.realms.AssistanceAgentsRepository;

@Validator
public class AssistanceAgentsValidator extends AbstractValidator<ValidAssistanceAgents, AssistanceAgents> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentsRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAssistanceAgents annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgents assistanceAgents, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (assistanceAgents == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String name = assistanceAgents.getIdentity().getName();
			String surname = assistanceAgents.getIdentity().getSurname();

			boolean correctEmployeeCode = assistanceAgents.getEmployeeCode().charAt(0) == name.charAt(0) && assistanceAgents.getEmployeeCode().charAt(1) == surname.charAt(0);

			super.state(context, correctEmployeeCode, "employeeCode", "acme.validation.assistanceAgents.employeeCode.message");
		}

		result = !super.hasErrors(context);

		return result;
	}
}
