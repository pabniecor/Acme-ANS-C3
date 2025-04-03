
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.BookingRecord;
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
		status = booking != null && booking.getDraftMode() == true && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().setAuthorised(status);
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
		BookingRecord bookingRecord;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);
		bookingRecord = new BookingRecord();
		bookingRecord.setBooking(booking);
		bookingRecord.setPassenger(passenger);
		this.repository.save(passenger);
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final Passenger passenger) {

		Dataset dataset;
		Collection<Customer> customers;
		SelectChoices choicesCustomer;

		customers = this.repository.findAllCustomers();
		choicesCustomer = SelectChoices.from(customers, "identifier", passenger.getCustomer());

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "birthDate", "specialNeeds", "draftModePassenger");
		dataset.put("bookingId", super.getRequest().getData("bookingId", int.class));

		dataset.put("customers", choicesCustomer);
		dataset.put("customer", choicesCustomer.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
