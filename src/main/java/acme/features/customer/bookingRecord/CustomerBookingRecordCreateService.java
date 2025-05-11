
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
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = false;

		try {
			int bookingId = super.getRequest().getData("bookingId", int.class);
			Booking booking = this.repository.findBookingById(bookingId);
			Customer currentCustomer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

			if (booking != null && currentCustomer != null) {
				status = booking.getCustomer().getId() == currentCustomer.getId();

				if (status && super.getRequest().getMethod().equals("POST"))
					if (super.getRequest().hasData("passenger")) {
						Integer passengerId = super.getRequest().getData("passenger", Integer.class);

						if (passengerId != null && passengerId > 0) {
							Passenger passenger = this.repository.findPassengerById(passengerId);

							status = passenger != null && passenger.getCustomer().getId() == currentCustomer.getId();

							if (status) {
								Collection<Passenger> assignedPassengers = this.repository.findAssignedPassengersByBookingId(bookingId);
								status = !assignedPassengers.contains(passenger);
							}
						} else
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
		BookingRecord bookingRecord;
		Booking booking;
		int bookingId;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);

		bookingRecord = new BookingRecord();
		bookingRecord.setBooking(booking);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		super.bindObject(bookingRecord, "passenger");
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		;
	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;
		Collection<Passenger> assignedPassengers;
		Collection<Passenger> notAssignedPassengers;
		SelectChoices choicesPassengers;

		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		int bookingId = super.getRequest().getData("bookingId", int.class);

		assignedPassengers = this.repository.findAssignedPassengersByBookingId(bookingId);
		notAssignedPassengers = this.repository.findPassengersByCustomerId(customerId).stream().filter(p -> !assignedPassengers.contains(p)).toList();
		choicesPassengers = SelectChoices.from(notAssignedPassengers, "fullName", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord, "passenger", "booking");
		dataset.put("passengers", choicesPassengers);

		super.getResponse().addData(dataset);
	}
}
