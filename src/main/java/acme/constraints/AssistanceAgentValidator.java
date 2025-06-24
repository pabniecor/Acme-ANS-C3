
package acme.constraints;

import java.util.Collection;

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
				boolean uniqueEmployeeCode;
				Collection<String> allEmployeeCodes = this.repository.findAllEmployeeCodes();
				String employeeCode = assistanceAgent.getEmployeeCode();

				if (allEmployeeCodes != null && employeeCode != null) {
					uniqueEmployeeCode = allEmployeeCodes.stream().filter(ec -> ec.equals(employeeCode)).toList().size() == 1;

					super.state(context, uniqueEmployeeCode, "employeeCode", "acme.validation.assistanceAgent.uniqueEmployeeCode.message");
				}
			}

		}

		result = !super.hasErrors(context);

		return result;
	}
}
