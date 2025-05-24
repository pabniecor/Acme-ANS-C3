
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	protected ManagerFlightRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);
		Manager manager = flight == null ? null : flight.getManager();
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		//Iterador para ver si todas las legs estan en draftMode
		int numLegsPublished = 0;
		for (Leg leg : legs)
			if (leg.getDraftMode().equals(false))
				numLegsPublished++;
		boolean status = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && flight.getDraftMode() && numLegsPublished == 0;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		;
	}

	@Override
	public void validate(final Flight flight) {
		;
	}

	@Override
	public void perform(final Flight flight) {
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());

		//		for (Leg leg : legs) {
		//			Collection<FlightAssignment> flightAssignments = this.repository.findFlightAssignmentsByLegId(leg.getId());
		//			Collection<Claim> claims = this.repository.findClaimsByLegId(leg.getId());
		//			this.repository.deleteAll(flightAssignments);
		//			this.repository.deleteAll(claims);
		//		}

		this.repository.deleteAll(legs);

		//		Collection<Booking> bookings = this.repository.findBookingsByFlightId(flight.getId());
		//		this.repository.deleteAll(bookings);

		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");

		dataset.put("departure", flight.getDeparture());
		dataset.put("arrival", flight.getArrival());
		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		dataset.put("layovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}
}
