
package acme.entities.airport_management;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirportServiceRepository extends AbstractRepository {

	@Query("select s.promotionCode from Service s")
	Collection<String> findAllPromotionCodes();
}
