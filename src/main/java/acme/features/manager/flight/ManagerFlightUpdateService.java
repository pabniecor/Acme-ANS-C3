
package acme.features.manager.flight;

import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	protected ManagerFlightRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);

		boolean authorised = flight != null && super.getRequest().getPrincipal().hasRealm(flight.getManager()) && flight.getDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		assert flight != null;
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;
		if (super.getRequest().getCommand().equalsIgnoreCase("update")) {
			Flight original = this.repository.findFlightById(flight.getId());

			boolean isModified = !Objects.equals(flight.getTag(), original.getTag()) || !Objects.equals(flight.getDescription(), original.getDescription()) || !Objects.equals(flight.getSelfTransfer(), original.getSelfTransfer())
				|| !Objects.equals(flight.getCost().getAmount(), original.getCost().getAmount()) || !Objects.equals(flight.getCost().getCurrency(), original.getCost().getCurrency());

			super.state(isModified, "*", "manager.flight.error.no-changes");
		}
	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description");

		dataset.put("id", flight.getId());
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
