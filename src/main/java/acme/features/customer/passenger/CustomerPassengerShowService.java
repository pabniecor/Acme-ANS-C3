
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerShowService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);

		if (!super.getRequest().getData().isEmpty()) {
			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int passengerId = super.getRequest().getData("id", int.class);
			Passenger passenger = this.repository.findPassengerById(passengerId);

			super.getResponse().setAuthorised(customerId == passenger.getCustomer().getId());
		}
	}

	@Override
	public void load() {
		Passenger passenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		Collection<Customer> customers;
		SelectChoices choicesCustomer;

		customers = this.repository.findAllCustomers();
		choicesCustomer = SelectChoices.from(customers, "identifier", passenger.getCustomer());

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "birthDate", "specialNeeds", "draftModePassenger");

		dataset.put("customer", choicesCustomer.getSelected().getKey());
		dataset.put("customers", choicesCustomer);

		super.getResponse().addData(dataset);
	}

}
