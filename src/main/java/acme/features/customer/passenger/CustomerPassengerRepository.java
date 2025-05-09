
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findAssignedPassengersByBookingId(int bookingId);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Passenger findAssignedPassengerByBookingId(int bookingId);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Passenger findPassengerByBookingId(int bookingId);

	@Query("select b from Booking b")
	Collection<Booking> findAllBookings();

	@Query("select b from Booking  b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select br.booking from BookingRecord br where br.passenger.id = :id")
	Booking findBookingByPassengerId(int id);

	@Query("select p from Passenger p where p.customer.id = :id")
	Collection<Passenger> findPassengersByCustomerId(int id);

	@Query("select p from Passenger p where p.customer.id = :id")
	Passenger findPassengerByCustomerId(int id);

	@Query("select c from Customer c")
	Collection<Customer> findAllCustomers();
}
