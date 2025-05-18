
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
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);
	}

	@Override
	public void load() {
		Passenger passenger;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		passenger = new Passenger();
		passenger.setCustomer(customer);
		passenger.setDraftModePassenger(true);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "birthDate", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
		;
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		assert passenger != null;

		Dataset dataset;
		Collection<Customer> customers;
		SelectChoices choicesCustomer;

		customers = this.repository.findAllCustomers();
		choicesCustomer = SelectChoices.from(customers, "identifier", passenger.getCustomer());

		dataset = super.unbindObject(passenger, "id", "fullName", "email", "passportNumber", "birthDate", "specialNeeds", "draftModePassenger", "customer");

		dataset.put("customers", choicesCustomer);
		dataset.put("customer", choicesCustomer.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
