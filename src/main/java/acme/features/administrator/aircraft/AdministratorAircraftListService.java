
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;

@GuiService
public class AdministratorAircraftListService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		Boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Aircraft> aircrafts;

		aircrafts = this.repository.findAllAircrafts();

		super.getBuffer().addData(aircrafts);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
		dataset.put("airline", aircraft.getAirline().getName());
		super.getResponse().addData(dataset);
	}
}
