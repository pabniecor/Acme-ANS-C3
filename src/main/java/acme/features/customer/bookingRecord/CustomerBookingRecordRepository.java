package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.BookingRecord;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select b from Booking  b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.customer.id = :id")
	Collection<Passenger> findPassengersByCustomerId(int id);

	@Query("select br from BookingRecord br where br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordsByBookingId(int bookingId);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findAssignedPassengersByBookingId(int bookingId);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Passenger findAssignedPassengerByBookingId(int bookingId);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select br.booking from BookingRecord br where br.passenger.id = :id")
	Booking findBookingByPassengerId(int id);

	@Query("select c from Customer c")
	Collection<Customer> findAllCustomers();

	@Query("select br from BookingRecord br where br.id = :id")
	BookingRecord findBookingRecordById(int id);

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select br.booking from BookingRecord br where br.id = :id")
	Booking findBookingByBookingRecordId(int id);

	@Query("select p from Passenger p where p.customer.id = :customerId and p not in (select br.passenger from BookingRecord br where br.booking.id = :bookingId)")
	Collection<Passenger> findNotAssignedPassengersByCustomerAndBookingId(int customerId, int bookingId);

}
