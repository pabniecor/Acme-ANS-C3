
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Customer;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (customer == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String name = customer.getIdentity().getName();
			String surname = customer.getIdentity().getSurname();

			boolean correctIdentifier = customer.getIdentifier().charAt(0) == name.charAt(0) && customer.getIdentifier().charAt(1) == surname.charAt(0);
			super.state(context, correctIdentifier, "identifier", "acme.constraints.customer.not-corresponding-credentials.message");
		}

		result = !super.hasErrors(context);
		return result;
	}

}
