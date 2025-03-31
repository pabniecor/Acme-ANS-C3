
package acme.features.flightCrewMember.flightCrewMember;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.realms.FlightCrewMember;

@Repository
public interface MemberFlightCrewMemberRepository extends AbstractRepository {

	@Query("select fa.flightCrew from FlightAssignment fa where fa.id=:id")
	Collection<FlightCrewMember> findMembersById(int id);
}
