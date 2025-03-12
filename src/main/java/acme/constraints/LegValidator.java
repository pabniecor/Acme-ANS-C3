
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flight_management.Leg;
import acme.entities.flight_management.LegRepository;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private LegRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (leg == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				boolean uniqueLeg;
				Leg existingLeg;

				existingLeg = this.repository.findLegByFlightNumber(leg.getFlightNumber());
				uniqueLeg = existingLeg == null || existingLeg.equals(leg);

				super.state(context, uniqueLeg, "flightNumber", "The flightNumber is duplicated");
			}
			{
				String AirlineIATA = leg.getFlight().getManager().getAirline().getIataCode();

				boolean correctFlightNumber = leg.getFlightNumber().substring(0, 3).equals(AirlineIATA);

				super.state(context, correctFlightNumber, "flightNumber", "The flightNumber IATA code is incorrect");
			}

			{
				boolean notOverlappingLeg;
				List<Leg> legsByFlight;
				Leg currectLeg, previousLeg;

				legsByFlight = this.repository.computeLegsByFlight(leg.getFlight().getId());

				legsByFlight.removeIf(l -> l.getFlightNumber().equals(leg.getFlightNumber()));

				legsByFlight.add(leg);

				//				legsByFlight.sort(Comparator.comparing(Leg::getSequenceOrder));
				int numOverlappedLegs = 0;
				for (int i = 1; i < legsByFlight.size(); i++) {

					currectLeg = legsByFlight.get(i);
					previousLeg = legsByFlight.get(i - 1);

					if (MomentHelper.isBeforeOrEqual(currectLeg.getScheduledDeparture(), previousLeg.getScheduledArrival()))
						numOverlappedLegs = numOverlappedLegs + 1;
				}
				notOverlappingLeg = numOverlappedLegs != 0;
				super.state(context, notOverlappingLeg, "*", "The leg can not overlap other legs in the same flight");
			}
			//			{
			//				boolean correctScheduledDeparture;
			//				Date currentMoment = MomentHelper.getCurrentMoment();
			//
			//				correctScheduledDeparture = MomentHelper.isBefore(leg.getScheduledDeparture(), currentMoment);
			//
			//				super.state(context, correctScheduledDeparture, "scheduledDeparture", "The scheduledDeparture can not be before the current time");
			//			}
			//			{
			//				boolean correctScheduledArrival;
			//				Date minMoment = MomentHelper.deltaFromMoment(leg.getScheduledDeparture(), 1, ChronoUnit.MINUTES);
			//
			//				correctScheduledArrival = MomentHelper.isBefore(leg.getScheduledArrival(), minMoment);
			//
			//				super.state(context, correctScheduledArrival, "scheduledArrival", "The scheduledArrival can not be before the scheduledDeparture + 1 minute");
			//			}

		}

		result = !super.hasErrors(context);

		return result;
	}

}
