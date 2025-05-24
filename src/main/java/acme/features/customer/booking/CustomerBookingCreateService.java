
package acme.features.customer.booking;

import java.util.ArrayList;
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

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().getMethod().equals("POST")) {
			if (super.getRequest().hasData("flight")) {
				int flightId = super.getRequest().getData("flight", int.class);
				if (flightId != 0) {
					Flight flight = this.repository.findFlightById(flightId);
					if (flight == null || flight.getDraftMode())
						status = false;
				}
			}

			if (status && super.getRequest().hasData("travelClass")) {
				TravelClass travelClass = super.getRequest().getData("travelClass", TravelClass.class);
				if (travelClass != null) {
					Collection<TravelClass> travelClasses = this.repository.findAllTravelClasses();
					if (!travelClasses.contains(travelClass))
						status = false;
				}
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
		Dataset dataset;
		Collection<Flight> allFlights;
		Collection<Flight> filteredFlights;
		SelectChoices travelClass;
		Date currentMoment;

		allFlights = this.repository.findAllPublishedFlights();
		filteredFlights = new ArrayList<>();
		currentMoment = MomentHelper.getCurrentMoment();

		for (Flight flight : allFlights)
			if (!flight.getDraftMode() && flight.getDeparture() != null && flight.getDeparture().after(currentMoment) && flight.getLayovers() != null && flight.getLayovers() > 0)
				filteredFlights.add(flight);

		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "flight", "draftMode", "id");
		dataset.put("travelClass", travelClass);

		SelectChoices flightChoices = null;
		flightChoices = SelectChoices.from(filteredFlights, "bookingFlight", booking.getFlight());

		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
