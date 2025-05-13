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
		
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		
		if (status) {
			customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			
			if (super.getRequest().hasData("id"))
				bookingId = super.getRequest().getData("id", int.class);
			
			booking = this.repository.findBookingById(bookingId);
			
			if (booking != null) {
				status = booking.getDraftMode() && booking.getCustomer().getId() == customerId;
				
				if (status && super.getRequest().getMethod().equals("POST") && 
					super.getRequest().hasData("flight")) {
					
					flightId = super.getRequest().getData("flight", int.class);
					flight = this.repository.findFlightById(flightId);
					
					status = flight != null && 
							!flight.getDraftMode() && 
							flight.getDeparture() != null && 
							flight.getDeparture().after(MomentHelper.getCurrentMoment()) && 
							flight.getLayovers() != null && 
							flight.getLayovers() > 0;
				}
			} else {
				status = false;
			}
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
		//		Collection<Flight> availableFlights = allFlights.stream().filter(f -> !f.getDraftMode()).filter(f -> f.getDeparture() != null && f.getDeparture().after(MomentHelper.getCurrentMoment())).filter(f -> f.getLayovers() != null && f.getLayovers() > 0)
		//			.collect(Collectors.toList());

		flightChoices = SelectChoices.from(flights, "bookingFlight", booking.getFlight());

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "price", "lastCardNibble", "id", "draftMode");

		dataset.put("travelClass", travelClass);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}
}
