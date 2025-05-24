
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.FlightRepository;
import acme.entities.flight_management.Leg;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	// Internal state ---------------------------------------------------------
	@Autowired
	FlightRepository flightRepository;
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
		else if (!flight.getDraftMode()) {
			{
				boolean notOverlapping;
				List<Leg> legsByFlight;
				Leg currentLeg;
				Leg nextLeg;
				legsByFlight = this.flightRepository.computeLegsByFlight(flight.getId());
				int overlappedLegs = 0;
				for (int i = 0; i < legsByFlight.size() - 1; i++) {
					currentLeg = legsByFlight.get(i);
					nextLeg = legsByFlight.get(i + 1);
					if (MomentHelper.isAfter(currentLeg.getScheduledArrival(), nextLeg.getScheduledDeparture()))
						overlappedLegs = overlappedLegs + 1;
				}
				notOverlapping = overlappedLegs == 0;
				super.state(context, notOverlapping, "*", "acme.validation.flight.overlapped.message");
			}
			{
				boolean correctAirportMatches;
				List<Leg> legsByFlight;

				legsByFlight = this.flightRepository.computeLegsByFlight(flight.getId());
				int noMatchedAirports = 0;
				for (int i = 0; i < legsByFlight.size() - 1; i++) {
					String arriveIataCode = legsByFlight.get(i).getArrivalAirport().getIataCode();
					String departNextIataCode = legsByFlight.get(i + 1).getDepartureAirport().getIataCode();
					if (!arriveIataCode.equals(departNextIataCode))
						noMatchedAirports += 1;
				}
				correctAirportMatches = noMatchedAirports == 0;
				super.state(context, correctAirportMatches, "*", "acme.validation.flight.matchAirports.message");
			}
		}
		result = !super.hasErrors(context);

		return result;

	}
}
