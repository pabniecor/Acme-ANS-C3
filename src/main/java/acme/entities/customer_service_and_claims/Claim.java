
package acme.entities.customer_service_and_claims;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.flight_management.Leg;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "assistanceAgent_id"), @Index(columnList = "assistanceAgent_id,draftMode"), @Index(columnList = "leg_id,registrationMoment")
})
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidString(max = 255, min = 1)
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;


	@Transient
	public AcceptanceStatus getAccepted() {
		AcceptanceStatus result = AcceptanceStatus.PENDING;
		ClaimRepository repository = SpringHelper.getBean(ClaimRepository.class);

		List<TrackingLog> orderedTrackingLogs = repository.getTrackingLogsByResolutionPercentageOrder(this.getId());

		if (orderedTrackingLogs != null && !orderedTrackingLogs.isEmpty()) {
			TrackingLog highestResolutionPercentageTrackingLog = orderedTrackingLogs.get(0);
			if (highestResolutionPercentageTrackingLog != null)
				if (highestResolutionPercentageTrackingLog.getStatus().equals(AcceptanceStatus.ACCEPTED))
					result = AcceptanceStatus.ACCEPTED;
				else if (highestResolutionPercentageTrackingLog.getStatus().equals(AcceptanceStatus.REJECTED))
					result = AcceptanceStatus.REJECTED;
		}
		return result;
	}


	@Mandatory
	@Valid
	@Automapped
	private Boolean			draftMode;

	// Relationships -----------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent	assistanceAgent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg				leg;
}
