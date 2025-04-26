
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.TravelClass;
import acme.entities.flight_management.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;
		Customer customer;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();
		status = booking != null && booking.getDraftMode() == true && super.getRequest().getPrincipal().hasRealm(customer);

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
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;

		Dataset dataset;
		Collection<Customer> customers;
		Collection<Flight> flights;
		SelectChoices choicesCustomer;
		SelectChoices choicesFlight;
		SelectChoices travelClass;

		flights = this.repository.findAllFlights();
		choicesFlight = SelectChoices.from(flights, "bookingFlight", booking.getFlight());
		customers = this.repository.findAllCustomers();
		choicesCustomer = SelectChoices.from(customers, "identifier", booking.getCustomer());
		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode", "flight");
		dataset.put("travelClass", travelClass);

		dataset.put("flight", choicesFlight.getSelected().getKey());
		dataset.put("flights", choicesFlight);

		dataset.put("customer", choicesCustomer.getSelected().getKey());
		dataset.put("customers", choicesCustomer);

		super.getResponse().addData(dataset);
	}

}
