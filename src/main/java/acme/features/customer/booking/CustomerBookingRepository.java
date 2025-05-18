
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.customer_management.Booking;
import acme.entities.customer_management.Passenger;
import acme.entities.customer_management.TravelClass;
import acme.entities.flight_management.Flight;
import acme.realms.Customer;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b")
	Collection<Booking> findAllBookings();

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select c from Customer c")
	Collection<Customer> findAllCustomers();

	@Query("select c from Customer c where c.id =:id")
	Customer findCustomerById(int id);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(int customerId);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Booking findBookingByCustomerId(int customerId);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select f from Flight f where f.draftMode = false")
	Collection<Flight> findAllPublishedFlights();

	@Query("select distinct(b.travelClass) from Booking b")
	Collection<TravelClass> findAllTravelClasses();

}
