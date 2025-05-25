
package acme.constraints;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.entities.flight_management.LegRepository;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private LegRepository repository;
	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidFlight annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (flight == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else {

			List<Leg> legs = this.repository.findLegsOrderByAscendentUsingSequenceOrder(flight.getId());

			Boolean validFlight = true;
			if (flight.getDraftMode().equals(true))
				validFlight = true;
			else {
				for (int i = 1; i < legs.size(); i++) {
					Date previousArrival = legs.get(i - 1).getScheduledArrival();
					Date currentDeparture = legs.get(i).getScheduledDeparture();

					validFlight = validFlight && MomentHelper.isAfter(currentDeparture, previousArrival);
				}
				super.state(context, validFlight, "scheduledArrival", "acme.validation.flight.overlappedLegs.message");

				boolean legsStatus = flight.getDraftMode().equals(true) || !legs.isEmpty() && legs.stream().allMatch(l -> l.getDraftMode() == false);
				super.state(context, legsStatus, "*", "acme.validation.flight.nonPublishedLegs.message");
			}
		}
		result = !super.hasErrors(context);

		return result;

	}
}
