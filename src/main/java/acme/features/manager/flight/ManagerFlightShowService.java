
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_management.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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
		Collection<Manager> managers;
		SelectChoices choises;

		managers = this.repository.findAllManagers();
		choises = SelectChoices.from(managers, "identifier", flight.getManager());

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "manager");
		dataset.put("manager", choises.getSelected().getKey());
		dataset.put("managers", choises);

		super.getResponse().addData(dataset);
	}
}
