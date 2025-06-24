
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidTechnician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTechnician
@Table(indexes = {
	@Index(columnList = "licenseNumber"), @Index(columnList = "user_account_id")
})
public class Technician extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", message = "{acme.validation.technician.correctLicenseNumberPattern.message}")
	@Column(unique = true)
	private String				licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$", message = "{acme.validation.technician.correctPhoneNumberPattern.message}")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				specialisation;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				healthTestPassed;

	@Mandatory
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private Integer				experience;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				certifications;

}
