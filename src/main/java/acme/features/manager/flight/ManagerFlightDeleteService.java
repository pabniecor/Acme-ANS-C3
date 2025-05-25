
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
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

		boolean status = flight != null && flight.getDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);

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
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		boolean validFlight = true;
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		Collection<Booking> bookings = this.repository.findBookingsByFlightId(flight.getId());

		for (Leg l : legs)
			if (l.getDraftMode().equals(false)) {
				validFlight = false;
				super.state(validFlight, "*", "acme.validation.flight.canNotDelete.message");
				break;
			}

		for (Booking b : bookings)
			if (b.getDraftMode().equals(false)) {
				validFlight = false;
				super.state(validFlight, "*", "acme.validation.flight.canNotDelete.message");
				break;
			}
	}

	@Override
	public void perform(final Flight flight) {
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		Collection<Booking> bookings = this.repository.findBookingsByFlightId(flight.getId());

		this.repository.deleteAll(legs);
		this.repository.deleteAll(bookings);
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");
		dataset.put("departure", flight.getDeparture());
		dataset.put("arrival", flight.getArrival());
		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		dataset.put("layovers", flight.getLayovers());

		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		boolean hasLegs = !legs.isEmpty();
		boolean canPublish = hasLegs && legs.stream().noneMatch(Leg::getDraftMode);

		dataset.put("canPublish", canPublish);

		super.getResponse().addData(dataset);
	}
}
