
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Customer;
import acme.realms.CustomerRepository;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Autowired
	private CustomerRepository repository;


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

			boolean uniqueCustomer;
			Customer existingCustomer;

			existingCustomer = this.repository.findCustomerByIdentifier(customer.getIdentifier());
			uniqueCustomer = existingCustomer == null || existingCustomer.equals(customer);

			super.state(context, uniqueCustomer, "identifier", "acme.validation.customer.duplicate-identifier.message");
		}
		{
			boolean isValidIdentifier;

			isValidIdentifier = customer.getIdentifier().matches("^[A-Z]{2,3}\\d{6}$");
			super.state(context, isValidIdentifier, "identifier", "acme.validation.customer.identifier.message");
		}
		{
			String name = customer.getIdentity().getName();
			String surname = customer.getIdentity().getSurname();

			boolean correctIdentifier = customer.getIdentifier().charAt(0) == name.charAt(0) && customer.getIdentifier().charAt(1) == surname.charAt(0);
			super.state(context, correctIdentifier, "identifier", "acme.validation.customer.identifier.message");
		}

		result = !super.hasErrors(context);
		return result;
	}

}
