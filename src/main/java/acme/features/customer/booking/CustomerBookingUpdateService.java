package acme.features.customer.booking;

import java.util.Collection;

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
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int customerId = 0;
		int bookingId = 0;
		int flightId = 0;
		Booking booking = null;
		Flight flight = null;
		TravelClass travelClass = null;
		Collection<TravelClass> travelClasses;

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
						flightId = super.getRequest().getData("flight", int.class);
						flight = this.repository.findFlightById(flightId);
						status = flight != null && !flight.getDraftMode() && flight.getDeparture() != null && flight.getDeparture().after(MomentHelper.getCurrentMoment()) && flight.getLayovers() != null && flight.getLayovers() > 0;
					}

					if (super.getRequest().hasData("travelClass")) {
						travelClass = super.getRequest().getData("travelClass", TravelClass.class);
						travelClasses = this.repository.findAllTravelClasses();
						status = status && (travelClass == null || travelClasses.contains(travelClass));
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
		;
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
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

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "price", "lastCardNibble", "id", "draftMode");

		dataset.put("travelClass", travelClass);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}
}
