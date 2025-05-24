
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
		boolean status = false;
		int customerId = 0;
		int bookingId = 0;
		Booking booking = null;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status) {
			customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (super.getRequest().hasData("id"))
				bookingId = super.getRequest().getData("id", int.class);

			booking = this.repository.findBookingById(bookingId);

			if (booking != null) {
				status = booking.getDraftMode() && booking.getCustomer().getId() == customerId;

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
		boolean publishedFlight = booking.getFlight() != null && !booking.getFlight().getDraftMode();
		super.state(publishedFlight, "flight", "acme.validation.customer.publishedFlight.message");
	}

	@Override
	public void perform(final Booking booking) {
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

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "purchaseMoment", "price", "lastCardNibble", "id", "draftMode");

		dataset.put("travelClass", travelClass);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}
}
