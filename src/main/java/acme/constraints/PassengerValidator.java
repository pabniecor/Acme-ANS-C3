
package acme.constraints;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.entities.customer_management.Passenger;

public class PassengerValidator extends AbstractValidator<ValidPassenger, Passenger> {

	@Override
	public boolean isValid(final Passenger passenger, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (passenger == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String passportNumber;
			boolean isValidPassportNumber;

			passportNumber = passenger.getPassportNumber();
			isValidPassportNumber = passportNumber != null && Pattern.matches("^[A-Z0-9]{6,9}$", passportNumber);

			super.state(context, isValidPassportNumber, "passportNumber", "acme.validation.passenger.passportNumber.message");
		}

		result = !super.hasErrors(context);

		return result;
	}
}
