
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airline_operations.AircraftStatus;
import acme.entities.airline_operations.Airline;

@GuiService
public class AdministratorAircraftDeleteService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		AircraftStatus status;

		status = super.getRequest().getData("status", AircraftStatus.class);
		if (status == AircraftStatus.ACTIVE_SERVICE)
			super.state(status == AircraftStatus.ACTIVE_SERVICE, "*", "administrator.aircraft.delete.activeService");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.delete(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		Collection<Airline> airlines;
		SelectChoices choises;
		SelectChoices aircraftStatus;

		airlines = this.repository.findAllAirlines();
		aircraftStatus = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		choises = SelectChoices.from(airlines, "name", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
		dataset.put("airline", choises.getSelected().getKey());
		dataset.put("airlines", choises);
		dataset.put("status", aircraftStatus);

		super.getResponse().addData(dataset);
	}
}
