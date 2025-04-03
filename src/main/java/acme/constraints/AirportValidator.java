
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airport_management.Airport;
import acme.entities.airport_management.AirportRepository;

@Validator
public class AirportValidator extends AbstractValidator<ValidAirport, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AirportRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAirport annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airport airport, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (airport == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				boolean uniqueAirport;
				Airport existingAirport;

				existingAirport = this.repository.findAirportByIataCode(airport.getIataCode());
				uniqueAirport = existingAirport == null || existingAirport.getIataCode().isBlank() || existingAirport.equals(airport);

				super.state(context, uniqueAirport, "iataCode", "acme.validation.airport.duplicate-iataCode.message");
			}
			{
				boolean isValidIataCode;

				isValidIataCode = airport.getIataCode().matches("^[A-Z]{3}$");

				super.state(context, isValidIataCode, "iataCode", "acme.validation.airport.iataCode.message");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}
