
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.realms.Manager;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select f from Flight f where f.manager.identifier = :managerId")
	Collection<Flight> findFlightsByManagerId(int managerId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("select m from Manager m")
	Collection<Manager> findAllManagers();

	@Query("select m.id from Manager m where m.userAccount.id = :userAccountId")
	Integer findManagerByUserAccountId(int userAccountId);
}
