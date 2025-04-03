
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
		boolean status;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);
		status = booking != null && (booking.getDraftMode() == false || super.getRequest().getPrincipal().hasRealm(booking.getCustomer()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int bookingId;

		bookingId = super.getRequest().getData("bookingId", int.class);
		passengers = this.repository.findAssignedPassengersByBookingId(bookingId);

		super.getBuffer().addData(passengers);

	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "birthDate", "specialNeeds", "draftModePassenger");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Passenger> passengers) {
		int bookingId;
		Booking booking;
		final boolean showCreate;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);
		showCreate = booking.getDraftMode() == true && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().addGlobal("bookingId", bookingId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}
}
