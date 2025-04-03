
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
public class CustomerPassengerPublishService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	protected CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int passengerId;
		Passenger passenger;
		Customer customer;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);
		customer = passenger == null ? null : passenger.getCustomer();
		status = passenger != null && passenger.getDraftModePassenger() == true && super.getRequest().getPrincipal().hasRealm(customer);

		super.getResponse().setAuthorised(status);
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
	public void validate(final Passenger passenger) {
		;
	}

	@Override
	public void bind(final Passenger passenger) {
		;
	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setDraftModePassenger(false);
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

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "birthDate", "specialNeeds", "draftModePassenger");

		dataset.put("customer", choicesCustomer.getSelected().getKey());
		dataset.put("customers", choicesCustomer);

		super.getResponse().addData(dataset);
	}

}
