
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Manager;

@Validator
public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	// Internal state ---------------------------------------------------------

	//	@Autowired
	//	private ManagerRepository repository;

	// ConstraintValidator interface ------------------------------------------

	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		{
			String name = manager.getIdentity().getName();
			String surname = manager.getIdentity().getSurname();

			boolean correctIdentifier = manager.getIdentifier().charAt(0) == name.charAt(0) && manager.getIdentifier().charAt(1) == surname.charAt(0);

			super.state(context, correctIdentifier, "identifier", "The manager's identier does not correspond to their initials");
		}

		result = !super.hasErrors(context);

		return result;
	}
}
