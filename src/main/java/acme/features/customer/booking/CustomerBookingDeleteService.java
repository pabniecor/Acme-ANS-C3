
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.BookingRecord;
import acme.entities.customer_management.TravelClass;
import acme.entities.flight_management.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingDeleteService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Booking booking;
		Customer customer;
		String method;

		method = super.getRequest().getMethod();
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("id")) {
			id = super.getRequest().getData("id", int.class);
			booking = this.repository.findBookingById(id);
			customer = booking == null ? null : booking.getCustomer();

			status = booking != null && super.getRequest().getPrincipal().hasRealm(customer);

			if (method.equals("POST"))
				status = status && booking.getDraftMode();
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "lastCardNibble", "flight");
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		Collection<BookingRecord> bookingRecords;

		bookingRecords = this.repository.findBookingRecordsByBookingId(booking.getId());
		this.repository.deleteAll(bookingRecords);
		this.repository.delete(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices travelClass;
		Collection<Flight> flights;
		SelectChoices flightChoices;

		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		flights = this.repository.findAllFlights();

		flightChoices = SelectChoices.from(flights, "bookingFlight", booking.getFlight());

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "purchaseMoment", "price", "lastCardNibble", "id", "draftMode");

		dataset.put("travelClass", travelClass);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
