
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
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

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
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		Dataset dataset;
		Collection<Airline> airlines;
		SelectChoices choises;
		SelectChoices aircraftStatus;

		airlines = this.repository.findAllAirlines();
		aircraftStatus = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		choises = SelectChoices.from(airlines, "name", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
		int airlineId = Integer.valueOf(choises.getSelected().getKey());
		Airline airlinee = this.repository.findAirlineById(airlineId);
		dataset.put("airline", airlinee);
		dataset.put("airlines", choises);
		dataset.put("status", aircraftStatus);

		super.getResponse().addData(dataset);
	}
}
