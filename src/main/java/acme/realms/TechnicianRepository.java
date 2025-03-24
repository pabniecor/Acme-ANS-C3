
package acme.realms;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TechnicianRepository extends AbstractRepository {

	@Query("SELECT t.licenseNumber from Technician t")
	List<String> findAllLicenseNumbers();

}
