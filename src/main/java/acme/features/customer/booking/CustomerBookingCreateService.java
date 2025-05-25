
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
		boolean status;
		Flight flight;
		int flightId;
		TravelClass travelClass;
		Collection<TravelClass> travelClasses;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (super.getRequest().getMethod().equals("POST")) {
			boolean flightStatus = true;
			boolean travelClassStatus = true;

			if (super.getRequest().hasData("flight")) {
				flightId = super.getRequest().getData("flight", int.class);
				if (flightId != 0) {
					flight = this.repository.findFlightById(flightId);
					flightStatus = flight != null && !flight.getDraftMode();
				}
			}

			if (super.getRequest().hasData("travelClass")) {
				travelClass = super.getRequest().getData("travelClass", TravelClass.class);
				travelClasses = this.repository.findAllTravelClasses();
				travelClassStatus = travelClass == null ? true : travelClasses.contains(travelClass);
			}

			status = status && flightStatus && travelClassStatus;
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
		Dataset dataset;
		Collection<Flight> publishedFlights;
		SelectChoices travelClass;
		SelectChoices flightChoices;

		publishedFlights = this.repository.findAllPublishedFlights();

		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "flight", "draftMode", "id");
		dataset.put("travelClass", travelClass);

		flightChoices = SelectChoices.from(publishedFlights, "bookingFlight", booking.getFlight());
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
