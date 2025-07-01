
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerDeleteService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int customerId = 0;
		int passengerId = 0;
		Passenger passenger = null;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status) {
			customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (super.getRequest().hasData("id"))
				passengerId = super.getRequest().getData("id", int.class);

			passenger = this.repository.findPassengerById(passengerId);

			if (passenger != null)
				status = passenger.getDraftModePassenger() && passenger.getCustomer().getId() == customerId;
			else
				status = false;
		}

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
		;
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.delete(passenger);
	}

}
