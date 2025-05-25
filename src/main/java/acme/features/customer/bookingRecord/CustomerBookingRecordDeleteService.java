
package acme.features.customer.bookingRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.BookingRecord;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordDeleteService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int bookingRecordId;
		BookingRecord bookingRecord;
		Booking booking;
		Customer customer;
		String method;

		method = super.getRequest().getMethod();
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("id")) {
			bookingRecordId = super.getRequest().getData("id", int.class);
			bookingRecord = this.repository.findBookingRecordById(bookingRecordId);
			booking = bookingRecord == null ? null : bookingRecord.getBooking();
			customer = booking == null ? null : booking.getCustomer();

			status = bookingRecord != null && super.getRequest().getPrincipal().hasRealm(customer) && booking.getDraftMode();

		} else
			status = false;

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
	public void bind(final BookingRecord bookingRecord) {
		;
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		;
	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.delete(bookingRecord);
	}

}
