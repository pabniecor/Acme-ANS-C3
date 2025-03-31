
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
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

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
		;
	}

	@Override
	public void validate(final Flight flight) {
		;
	}

	@Override
	public void perform(final Flight flight) {
		Collection<Leg> legs;

		legs = this.repository.findLegsByFlightId(flight.getId());
		this.repository.deleteAll(legs);
		this.repository.delete(flight);
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

		dataset.put("sheduledDeparture", flight.getDeparture());
		dataset.put("sheduledArrival", flight.getArrival());
		dataset.put("departureCity", flight.getOriginCity());
		dataset.put("arrivalCity", flight.getDestinationCity());
		dataset.put("numberOfLayovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}
}
