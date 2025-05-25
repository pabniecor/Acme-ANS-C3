
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.AssistanceAgent;
import acme.realms.AssistanceAgentRepository;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent assistanceAgent, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (assistanceAgent == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				String name = assistanceAgent.getIdentity().getName();
				String surname = assistanceAgent.getIdentity().getSurname();

				boolean correctEmployeeCode = assistanceAgent.getEmployeeCode().charAt(0) == name.charAt(0) && assistanceAgent.getEmployeeCode().charAt(1) == surname.charAt(0);

				super.state(context, correctEmployeeCode, "employeeCode", "acme.validation.assistanceAgent.employeeCode.message");
			}

			{
				boolean correctEmployeeCodePattern;
				String employeeCoe = assistanceAgent.getEmployeeCode();

				if (employeeCoe != null) {
					correctEmployeeCodePattern = employeeCoe.matches("^[A-Z]{2,3}\\d{6}$");

					super.state(context, correctEmployeeCodePattern, "employeeCode", "acme.validation.technician.correctPhoneNumberPattern.message");
				}
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}
