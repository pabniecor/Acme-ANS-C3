
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
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Flight flight;
		Manager manager;

		masterId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(masterId);
		manager = flight == null ? null : flight.getManager();
		status = super.getRequest().getPrincipal().hasRealm(manager) || flight != null && !flight.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");

		dataset.put("sheduledDeparture", flight.getDeparture());
		dataset.put("sheduledArrival", flight.getArrival());
		dataset.put("departureCity", flight.getOriginCity());
		dataset.put("arrivalCity", flight.getDestinationCity());
		dataset.put("numberOfLayovers", flight.getLayovers());

		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		boolean hasLegs = !legs.isEmpty();
		boolean allPublished = hasLegs && legs.stream().noneMatch(Leg::getDraftMode);
		boolean canPublish = flight.getDraftMode() && allPublished;

		dataset.put("canPublish", canPublish);

		super.getResponse().addData(dataset);
	}
}
