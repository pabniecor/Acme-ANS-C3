
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingListService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);
	}

	@Override
	public void load() {
		Collection<Booking> bookings;
		int customerId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		bookings = this.repository.findBookingsByCustomerId(customerId);

		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode", "flight");

		super.getResponse().addData(dataset);
	}

}
