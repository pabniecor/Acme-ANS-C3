
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.Airport;
import acme.entities.airport_management.OperationalScope;

@GuiService
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository repository;


	@Override
	public void authorise() {
		Boolean status;
		int id = super.getRequest().getData("id", int.class);
		Airport airport = this.repository.findAirportById(id);

		status = airport != null && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (status) {
			String method;
			method = super.getRequest().getMethod();

			if (method.equals("GET"))
				status = true;
			else {
				@SuppressWarnings("unused")
				OperationalScope os = super.getRequest().getData("operationalScope", OperationalScope.class);
				status = true;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "email", "contactPhone");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;
		SelectChoices operationalScope;

		operationalScope = SelectChoices.from(OperationalScope.class, airport.getOperationalScope());

		dataset = super.unbindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "email", "contactPhone");
		dataset.put("operationalScope", operationalScope);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}
}
