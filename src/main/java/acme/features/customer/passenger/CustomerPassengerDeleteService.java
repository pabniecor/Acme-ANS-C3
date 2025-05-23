
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerDeleteService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int passengerId;
		Passenger passenger;
		Customer customer;
		String method;

		method = super.getRequest().getMethod();
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("id")) {
			passengerId = super.getRequest().getData("id", int.class);
			passenger = this.repository.findPassengerById(passengerId);
			customer = passenger == null ? null : passenger.getCustomer();

			status = passenger != null && super.getRequest().getPrincipal().hasRealm(customer);

			if (method.equals("POST"))
				status = status && passenger.getDraftModePassenger();
		} else
			status = false;

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
	public void bind(final Passenger passenger) {
		;
	}

	@Override
	public void validate(final Passenger passenger) {
		Booking booking;

		booking = this.repository.findBookingByPassengerId(passenger.getId());

		if (booking != null)
			super.state(false, "*", "acme.vaidation.customer.passenger.error.booking-exists");
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.delete(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		Collection<Customer> customers;
		SelectChoices choicesCustomer;

		customers = this.repository.findAllCustomers();
		choicesCustomer = SelectChoices.from(customers, "identifier", passenger.getCustomer());

		dataset = super.unbindObject(passenger, "id", "fullName", "email", "passportNumber", "birthDate", "specialNeeds", "draftModePassenger");

		dataset.put("customer", choicesCustomer.getSelected().getKey());
		dataset.put("customers", choicesCustomer);

		super.getResponse().addData(dataset);
	}
}
