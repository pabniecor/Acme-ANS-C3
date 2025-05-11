
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.Passenger;
import acme.entities.customer_management.TravelClass;
import acme.entities.flight_management.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	protected CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().getMethod().equals("POST"))
			try {
				int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				int bookingId = super.getRequest().getData("id", int.class);
				Booking booking = this.repository.findBookingById(bookingId);

				if (booking == null || !booking.getDraftMode() || booking.getCustomer().getId() != customerId)
					status = false;

				if (status && super.getRequest().hasData("travelClass")) {
					String travelClassValue = super.getRequest().getData("travelClass", String.class);

					try {
						if (travelClassValue != null)
							TravelClass.valueOf(travelClassValue);
					} catch (IllegalArgumentException e) {
						status = false;
					}
				}

				if (status && super.getRequest().hasData("flight") && super.getRequest().getData("flight", Integer.class) != null) {
					Integer flightId = super.getRequest().getData("flight", Integer.class);
					if (flightId > 0) {
						Flight flight = this.repository.findFlightById(flightId);
						if (flight == null || flight.getDraftMode() || flight.getDeparture() == null || !flight.getDeparture().after(MomentHelper.getCurrentMoment()) || flight.getLayovers() == null || flight.getLayovers() <= 0)
							status = false;
					}
				}
			} catch (Exception e) {
				status = false;
			}
		else
			// For GET requests, only check if the customer owns the booking
			try {
				int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				int bookingId = super.getRequest().getData("id", int.class);
				Booking booking = this.repository.findBookingById(bookingId);

				status = booking != null && booking.getDraftMode() && booking.getCustomer().getId() == customerId;
			} catch (Exception e) {
				status = false;
			}

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
	public void validate(final Booking booking) {
		String lastNibble = booking.getLastCardNibble();
		Collection<Passenger> bookingPassengers;

		bookingPassengers = this.repository.findPassengersByBookingId(booking.getId());
		boolean lastCardNibbleNotNull = !(lastNibble.isBlank() || booking.getLastCardNibble() == null);
		boolean passengersForBookingNotNull = !bookingPassengers.isEmpty();
		boolean publishedFlight = booking.getFlight().getDraftMode() == false;

		super.state(passengersForBookingNotNull, "*", "acme.validation.customer.passengersForBookingNotNull.message");
		super.state(lastCardNibbleNotNull, "lastCardNibble", "acme.validation.customer.lastCardNibble.message");
		super.state(publishedFlight, "flight", "acme.validation.customer.publishedFlight.message");
	}

	@Override
	public void bind(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices travelClass;
		SelectChoices choicesFlights;
		Collection<Flight> flights;
		SelectChoices flightChoices;

		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		flights = this.repository.findAllFlights();
		//		Collection<Flight> availableFlights = allFlights.stream().filter(f -> !f.getDraftMode()).filter(f -> f.getDeparture() != null && f.getDeparture().after(MomentHelper.getCurrentMoment())).filter(f -> f.getLayovers() != null && f.getLayovers() > 0)
		//			.collect(Collectors.toList());

		flightChoices = SelectChoices.from(flights, "bookingFlight", booking.getFlight());

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "price", "lastCardNibble", "id", "draftMode");

		dataset.put("travelClass", travelClass);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
