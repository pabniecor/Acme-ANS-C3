
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
		boolean status;
		int bookingRecordId;
		Booking booking;

		bookingRecordId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingByBookingRecordId(bookingRecordId);
		status = booking != null && (booking.getDraftMode() == false || super.getRequest().getPrincipal().hasRealm(booking.getCustomer()));

		super.getResponse().setAuthorised(status);
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

		passengers = this.repository.findAllPassengers();
		choicesPassengers = SelectChoices.from(passengers, "fullName", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord, "booking", "passenger");

		dataset.put("bookingId", bookingRecord.getBooking().getId());
		dataset.put("passengerId", choicesPassengers.getSelected().getKey());

		super.getResponse().addGlobal("passengers", choicesPassengers);
		super.getResponse().addData(dataset);
	}
}
