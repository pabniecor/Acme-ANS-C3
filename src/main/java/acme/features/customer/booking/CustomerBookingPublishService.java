
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
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
		boolean status = false;
		int customerId = 0;
		int bookingId = 0;
		Booking booking = null;
		Flight flight;
		int flightId;
		TravelClass travelClass;
		Collection<TravelClass> travelClasses;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status) {
			customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (super.getRequest().hasData("id"))
				bookingId = super.getRequest().getData("id", int.class);

			booking = this.repository.findBookingById(bookingId);

			if (booking != null) {
				status = booking.getDraftMode() && booking.getCustomer().getId() == customerId;

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
			} else
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
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "lastCardNibble", "flight");
	}

	@Override
	public void validate(final Booking booking) {
		Collection<Passenger> bookingPassengers = this.repository.findPassengersByBookingId(booking.getId());
		boolean passengersForBookingNotNull = !bookingPassengers.isEmpty();
		boolean lastCardNibbleNotNull = booking.getLastCardNibble() != null && !booking.getLastCardNibble().isBlank();
		boolean publishedFlight = booking.getFlight() != null && !booking.getFlight().getDraftMode();

		super.state(passengersForBookingNotNull, "*", "acme.validation.customer.passengersForBookingNotNull.message");
		super.state(lastCardNibbleNotNull, "lastCardNibble", "acme.validation.customer.lastCardNibble.message");
		super.state(publishedFlight, "flight", "acme.validation.customer.publishedFlight.message");
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
		Collection<Flight> publishedFlights;
		SelectChoices flightChoices;

		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		publishedFlights = this.repository.findAllPublishedFlights();

		flightChoices = SelectChoices.from(publishedFlights, "bookingFlight", booking.getFlight());

		dataset = super.unbindObject(booking, "flight", "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "id", "draftMode");

		dataset.put("travelClass", travelClass);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
