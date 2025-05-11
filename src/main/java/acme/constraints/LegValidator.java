
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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
		else if (leg.getFlightNumber() != null && leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
			boolean uniqueLeg;
			Leg existingLeg;

			existingLeg = this.repository.findLegByFlightNumber(leg.getFlightNumber());
			uniqueLeg = existingLeg == null || existingLeg.getFlightNumber().isBlank() || existingLeg.equals(leg);

			super.state(context, uniqueLeg, "flightNumber", "acme.validation.leg.duplicate-flightNumber.message");

			boolean isValidFlightNumber;

			isValidFlightNumber = leg.getFlightNumber().matches("^[A-Z]{3}\\d{4}$");

			super.state(context, isValidFlightNumber, "flightNumber", "acme.validation.leg.flightNumber.message");

			String AirlineIATA = leg.getFlight().getManager().getAirline().getIataCode();

			boolean correctFlightNumber = leg.getFlightNumber().length() >= 3 && leg.getFlightNumber().substring(0, 3).equals(AirlineIATA);

			super.state(context, correctFlightNumber, "flightNumber", "acme.validation.leg.flightNumber-airlineIATA.message");

			//			boolean notNullDates;
			//
			//			notNullDates = leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null;
			//
			//			super.state(context, notNullDates, "*", "acme.validation.leg.notNullDates.message");

			boolean notPastDeparture;

			notPastDeparture = !MomentHelper.isPast(leg.getScheduledDeparture());

			super.state(context, notPastDeparture, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.message");

			Date scheduledDeparture = leg.getScheduledDeparture();
			Date scheduledArrival = leg.getScheduledArrival();
			Date minimumArrival = MomentHelper.deltaFromMoment(scheduledDeparture, 1, ChronoUnit.MINUTES);
			Boolean validArrival = MomentHelper.isAfterOrEqual(scheduledArrival, minimumArrival);

			super.state(context, validArrival, "scheduledArrival", "acme.validation.leg.scheduledArrival.message");

			boolean notOverlapping = true;

			// Obtener todos los tramos del mismo vuelo ordenados por sequenceOrder
			List<Leg> legsByFlight = this.repository.findLegsOrderByAscendentUsingSequenceOrder(leg.getFlight().getId());

			// Filtrar la lista para excluir el tramo actual si ya existe (para casos de edición)
			List<Leg> filteredLegs = new ArrayList<>();
			for (Leg legToFilter : legsByFlight)
				if (leg.getFlightNumber() == null || !leg.getFlightNumber().equals(legToFilter.getFlightNumber()))
					filteredLegs.add(legToFilter);

			// Determinar la posición correcta para insertar el tramo actual según su sequenceOrder
			int insertPosition = 0;
			for (insertPosition = 0; insertPosition < filteredLegs.size(); insertPosition++)
				if (leg.getSequenceOrder() < filteredLegs.get(insertPosition).getSequenceOrder())
					break;

			// Insertar el tramo actual en la posición correcta
			filteredLegs.add(insertPosition, leg);

			// Verificar superposiciones
			for (int i = 1; i < filteredLegs.size(); i++) {
				Leg currentLeg = filteredLegs.get(i);
				Leg previousLeg = filteredLegs.get(i - 1);

				// Verificar si la salida del tramo actual es anterior a la llegada del tramo previo
				if (MomentHelper.isBefore(currentLeg.getScheduledDeparture(), previousLeg.getScheduledArrival())) {
					notOverlapping = false;
					break;
				}
			}

			super.state(context, notOverlapping, "scheduledDeparture", "acme.validation.leg.notOverlappingLeg.message");

			//			boolean notNullAirports;
			//
			//			notNullAirports = leg.getDepartureAirport() != null && leg.getArrivalAirport() != null;
			//
			//			super.state(context, notNullAirports, "*", "acme.validation.leg.notNullAirports.message");

			boolean notEqualAirports;

			notEqualAirports = leg.getDepartureAirport() != leg.getArrivalAirport();

			super.state(context, notEqualAirports, "arrivalAirport", "acme.validation.leg.arrivalAirport.message");
		}
		//		else if (leg.getFlightNumber() != null && leg.getScheduledDeparture() != null && leg.getScheduledDeparture() != null) {
		//
		//			boolean uniqueLeg;
		//			Leg existingLeg;
		//
		//			existingLeg = this.repository.findLegByFlightNumber(leg.getFlightNumber());
		//			uniqueLeg = existingLeg == null || existingLeg.getFlightNumber().isBlank() || existingLeg.equals(leg);
		//
		//			super.state(context, uniqueLeg, "flightNumber", "acme.validation.leg.duplicate-flightNumber.message");
		//
		//			Boolean matches;
		//			String airlineCode = leg.getFlight().getManager().getAirline().getIataCode();
		//			String flightNumber = leg.getFlightNumber();
		//
		//			matches = flightNumber.trim().startsWith(airlineCode.trim());
		//			super.state(context, matches, "flightNumber", "acme.validation.leg.flightNumber.message");
		//
		//			Date scheduledDeparture = leg.getScheduledDeparture();
		//			Date scheduledArrival = leg.getScheduledArrival();
		//			Date minimumArrival = MomentHelper.deltaFromMoment(scheduledDeparture, 1, ChronoUnit.MINUTES);
		//			Boolean validArrival = MomentHelper.isAfterOrEqual(scheduledArrival, minimumArrival);
		//			super.state(context, validArrival, "scheduledArrival", "acme.validation.leg.scheduledArrival.message");
		//		}
		//			{
		//				boolean notOverlappingLeg;
		//				List<Leg> legsByFlight;
		//				Leg currectLeg;
		//				Leg previousLeg;
		//
		//				legsByFlight = this.repository.findLegsOrderByAscendentUsingSequenceOrder(leg.getFlight().getId());
		//
		//				// Eliminar el tramo actual si ya existe en la lista (para evitar duplicados)
		//				List<Leg> filteredLegs = new ArrayList<>();
		//				for (Leg existingLeg : legsByFlight)
		//					if (!existingLeg.getFlightNumber().equals(leg.getFlightNumber()))
		//						filteredLegs.add(existingLeg);
		//
		//				// Determinar la posición correcta para insertar el tramo actual según su sequenceOrder
		//				int insertPosition = 0;
		//				for (insertPosition = 0; insertPosition < filteredLegs.size(); insertPosition++)
		//					if (leg.getSequenceOrder() < filteredLegs.get(insertPosition).getSequenceOrder())
		//						break;
		//
		//				//Integer lastSequenceOrder = this.repository.findLastSequenceOrderByFlightId(leg.getFlight().getId());
		//
		//				// Insertar el tramo actual en la posición correcta
		//				filteredLegs.add(insertPosition, leg);
		//
		//				int numOverlappedLegs = 0;
		//				for (int i = 1; i < legsByFlight.size(); i++) {
		//
		//					currectLeg = legsByFlight.get(i);
		//					previousLeg = legsByFlight.get(i - 1);
		//
		//					if (MomentHelper.isBefore(currectLeg.getScheduledDeparture(), previousLeg.getScheduledArrival())) {
		//						numOverlappedLegs++;
		//						break;
		//					}
		//				}
		//				notOverlappingLeg = numOverlappedLegs == 0;
		//				super.state(context, notOverlappingLeg, "scheduledDeparture", "acme.validation.leg.notOverlappingLeg.message");
		//			}

		result = !super.hasErrors(context);

		return result;
	}

}
