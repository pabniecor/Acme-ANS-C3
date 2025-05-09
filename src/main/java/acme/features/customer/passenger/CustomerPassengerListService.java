
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);

		if (!super.getRequest().getData().isEmpty()) {
			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int bookingId = super.getRequest().getData("bookingId", int.class);
			Booking booking = this.repository.findBookingById(bookingId);

			super.getResponse().setAuthorised(customerId == booking.getCustomer().getId());
		}

	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int customerId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (!super.getRequest().getData().containsKey("bookingId"))
			passengers = this.repository.findPassengersByCustomerId(customerId);
		else {
			Integer bookingId = super.getRequest().getData("bookingId", int.class);
			passengers = this.repository.findAssignedPassengersByBookingId(bookingId);
		}

		super.getBuffer().addData(passengers);

	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "birthDate", "specialNeeds", "draftModePassenger");

		super.getResponse().addData(dataset);
	}

}
