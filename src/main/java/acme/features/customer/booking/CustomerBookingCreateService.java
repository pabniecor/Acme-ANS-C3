
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

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

		if (status && super.getRequest().getMethod().equals("POST"))
			try {
				if (super.getRequest().hasData("travelClass")) {
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
						if (flight == null || flight.getDraftMode())
							status = false;
					}
				}
			} catch (Exception e) {
				status = false;
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
		//		if (booking.getFlight() != null) {
		//			boolean laterFlight = MomentHelper.isAfter(booking.getFlight().getDeparture(), MomentHelper.getCurrentMoment());
		//			super.state(laterFlight, "flight", "NOT POSSIBLE");
		//
		//		}
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {

		Dataset dataset;
		Collection<Flight> allFlights;
		Collection<Flight> availableFlights;
		SelectChoices travelClass;

		allFlights = this.repository.findAllFlights();
		availableFlights = allFlights.stream().filter(f -> !f.getDraftMode()).filter(f -> f.getDeparture() != null && f.getDeparture().after(MomentHelper.getCurrentMoment())).filter(f -> f.getLayovers() != null && f.getLayovers() > 0)
			.collect(Collectors.toList());
		travelClass = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "flight", "draftMode", "id");

		dataset.put("travelClass", travelClass);

		SelectChoices flightChoices = null;
		try {
			flightChoices = SelectChoices.from(availableFlights, "bookingFlight", booking.getFlight());
		} catch (Exception e) {
		}

		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
