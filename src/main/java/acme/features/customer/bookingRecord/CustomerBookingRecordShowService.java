package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.BookingRecord;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordShowService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);

		if (isCustomer && !super.getRequest().getData().isEmpty()) {
			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int bookingRecordId = super.getRequest().getData("id", int.class);
			BookingRecord bookingRecord = this.repository.findBookingRecordById(bookingRecordId);
			
			if (bookingRecord != null) {
				Booking booking = bookingRecord.getBooking();
				Passenger passenger = bookingRecord.getPassenger();
				
				boolean isAuthorised = booking.getCustomer().getId() == customerId && 
						passenger != null && 
						passenger.getCustomer().getId() == customerId;
						
				super.getResponse().setAuthorised(isAuthorised);
			} else {
				super.getResponse().setAuthorised(false);
			}
		}
	}

	@Override
	public void load() {
		BookingRecord bookingRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(id);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;
		Collection<Passenger> passengers;
		SelectChoices choicesPassengers;
		Booking booking;

		passengers = this.repository.findAllPassengers();
		choicesPassengers = SelectChoices.from(passengers, "fullName", bookingRecord.getPassenger());
		booking = bookingRecord.getBooking();

		dataset = super.unbindObject(bookingRecord, "booking", "passenger");

		dataset.put("bookingId", bookingRecord.getBooking().getId());
		dataset.put("passengerId", choicesPassengers.getSelected().getKey());
		dataset.put("passengerName", bookingRecord.getPassenger().getFullName());

		super.getResponse().addGlobal("booking", booking);
		super.getResponse().addGlobal("passengers", choicesPassengers);
		super.getResponse().addData(dataset);
	}
}
