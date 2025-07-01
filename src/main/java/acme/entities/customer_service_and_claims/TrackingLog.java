
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
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidScore;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidTrackingLog;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTrackingLog
@Table(indexes = {
	@Index(columnList = "claim_id,resolutionPercentage"), @Index(columnList = "claim_id,creationMoment"), @Index(columnList = "claim_id")
})
public class TrackingLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				creationMoment;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	private String				step;

	@Mandatory
	@ValidScore
	@Automapped
	private Double				resolutionPercentage;

	@Mandatory
	@Valid
	@Automapped
	private AcceptanceStatus	status;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				resolution;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				draftMode;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				reclaimed;


	@Transient
	public Boolean getReclaim() {
		boolean result = false;
		if (this.claim != null) {
			TrackingLogRepository repository = SpringHelper.getBean(TrackingLogRepository.class);

			List<TrackingLog> orderedTrackingLogs = repository.getTrackingLogsByResolutionPercentageOrder(this.claim.getId());

			if (orderedTrackingLogs != null && !orderedTrackingLogs.isEmpty()) {
				List<TrackingLog> trackingLogs100Percentage = orderedTrackingLogs.stream().filter(tl -> tl.getResolutionPercentage() == 100.).toList();
				TrackingLog highestResolutionPercentageTrackingLog = orderedTrackingLogs.get(0);

				if (highestResolutionPercentageTrackingLog.getResolutionPercentage() == 100. && trackingLogs100Percentage.size() == 1 && highestResolutionPercentageTrackingLog.getDraftMode() == false)
					result = true;
			}
		}

		return result;
	}

	// Relationships -----------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Claim claim;
}
