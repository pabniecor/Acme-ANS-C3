
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);
		status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);

		passenger = new Passenger();

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
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "birthDate", "specialNeeds");
		dataset.put("bookingId", super.getRequest().getData("bookingId", int.class));

		super.getResponse().addData(dataset);
	}
}
