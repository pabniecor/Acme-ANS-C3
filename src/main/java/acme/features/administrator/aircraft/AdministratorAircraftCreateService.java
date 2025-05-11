
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
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		Boolean status;
		Airline airline;
		int a;
		AircraftStatus st;
		Collection<AircraftStatus> sts;
		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		if (super.getRequest().hasData("id")) {
			airline = super.getRequest().getData("airline", Airline.class);
			a = super.getRequest().getData("airline", int.class);
			st = super.getRequest().getData("status", AircraftStatus.class);
			sts = this.repository.findAllStatus();
			Boolean statusAir = a == 0 ? true : this.repository.findAllAirlines().contains(airline);
			Boolean statusSt = st == null ? true : sts.contains(st);
			status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && statusAir && statusSt;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft;

		aircraft = new Aircraft();
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
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
