
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airline_operations.AircraftRepository;

@Validator
public class AircraftValidator extends AbstractValidator<ValidAircraft, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AircraftRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAircraft annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Aircraft value, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (value == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else if (value.getRegistrationNumber() != null) {
			boolean uniqueAircraft;
			Aircraft existingAircraft;
			existingAircraft = this.repository.findAircraftByRegNum(value.getRegistrationNumber());
			uniqueAircraft = existingAircraft == null || existingAircraft.getRegistrationNumber().isBlank() || existingAircraft.equals(value);
			super.state(context, uniqueAircraft, "registrationNumber", "acme.validation.aircraft.registrationNumber.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
