
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Airline;
import acme.entities.airline_operations.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratorAirlineRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		try {
			if (status && super.getRequest().getMethod().equals("POST")) {
				if (super.getRequest().hasData("id")) {
					int id = super.getRequest().getData("id", int.class);
					Airline airline = this.repository.findAirlinetById(id);
					status = airline != null;
				}
				if (status && super.getRequest().hasData("airlineType")) {
					String airlineTypeValue = super.getRequest().getData("airlineType", String.class);
					try {
						if (airlineTypeValue != null)
							AirlineType.valueOf(airlineTypeValue);
					} catch (IllegalArgumentException e) {
						status = false;
					}
				}

			}
		} catch (Exception e) {
			// Any exception results in unauthorized access
			status = false;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline;
		int id;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlinetById(id);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "airlineType", "foundationMoment", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		assert airline != null;
		Dataset dataset;
		SelectChoices airlineType;

		airlineType = SelectChoices.from(AirlineType.class, airline.getAirlineType());

		dataset = super.unbindObject(airline, "name", "iataCode", "website", "airlineType", "foundationMoment", "email", "phoneNumber");
		dataset.put("airlineType", airlineType);
		dataset.put("confirmation", false);
		super.getResponse().addData(dataset);
	}

}
