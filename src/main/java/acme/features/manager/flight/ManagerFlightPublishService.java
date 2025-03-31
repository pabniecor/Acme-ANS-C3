
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

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
		status = super.getRequest().getPrincipal().hasRealm(manager) && flight != null && flight.getDraftMode();

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
	public void bind(final Flight flight) {
		// No binding necesario para publicación
	}

	@Override
	public void validate(final Flight flight) {
		int flightId = flight.getId();
		Collection<Leg> legs = this.repository.findLegsByFlightId(flightId);

		boolean hasLegs = !legs.isEmpty();
		boolean allPublished = legs.stream().noneMatch(Leg::getDraftMode);

		super.state(hasLegs, "draftMode", "manager.flight.publish.error.no-legs");
		super.state(allPublished, "draftMode", "manager.flight.publish.error.unpublished-legs");
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;
		Dataset dataset;
		Collection<Manager> managers;
		SelectChoices choises;

		managers = this.repository.findAllManagers();
		choises = SelectChoices.from(managers, "identifier", flight.getManager());

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "manager", "draftMode");
		dataset.put("manager", choises.getSelected().getKey());
		dataset.put("managers", choises);

		super.getResponse().addData(dataset);
	}
}
