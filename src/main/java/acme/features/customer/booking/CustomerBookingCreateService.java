
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.TravelClass;
import acme.entities.flight_management.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (super.getRequest().hasData("id")) {
			Integer flightId = super.getRequest().getData("flight", int.class);
			if (flightId != null) {
				Flight flight = this.repository.findFlightById(flightId);
				status = status && flight != null && flight.getDraftMode() == false;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer customer;
		Booking booking;
		Date purchaseMoment;

		purchaseMoment = MomentHelper.getCurrentMoment();

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setPurchaseMoment(purchaseMoment);
		booking.setDraftMode(true);
		booking.setCustomer(customer);

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
		SelectChoices travelClass;
		Collection<Customer> customers;
		Collection<Flight> flights;
		SelectChoices choicesCustomer;
		SelectChoices choicesFlight;

		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		customers = this.repository.findAllCustomers();
		choicesCustomer = SelectChoices.from(customers, "identifier", booking.getCustomer());
		flights = this.repository.findAllFlights();
		choicesFlight = SelectChoices.from(flights, "bookingFlight", booking.getFlight());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode", "flight", "customer");

		dataset.put("travelClass", travelClass);

		dataset.put("flight", choicesFlight.getSelected().getKey());
		dataset.put("flights", choicesFlight);

		dataset.put("customer", choicesCustomer.getSelected().getKey());
		dataset.put("customers", choicesCustomer);

		super.getResponse().addData(dataset);
	}

}
