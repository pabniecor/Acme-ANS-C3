
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_management.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Flight> flights;

		flights = this.repository.findAllFlights();

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;
		Dataset dataset;
		boolean showCreate;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "manager", "draftMode");
		showCreate = super.getRequest().getPrincipal().hasRealm(flight.getManager());
		super.getResponse().addGlobal("showCreate", showCreate);

		super.getResponse().addData(dataset);
	}
}
