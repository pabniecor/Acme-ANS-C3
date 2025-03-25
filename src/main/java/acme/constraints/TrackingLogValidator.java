
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.customer_service_and_claims.TrackStatus;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.entities.customer_service_and_claims.TrackingLogRepository;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private TrackingLogRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (trackingLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			TrackStatus indicator = trackingLog.getStatus();

			if (indicator != TrackStatus.PENDING) {
				Double resolutionPercentage = trackingLog.getResolutionPercentage();
				String resolution = trackingLog.getResolution();
				boolean correctResolutionPercentage;
				boolean correctResolution;

				if (indicator == TrackStatus.ACCEPTED || indicator == TrackStatus.REJECTED) {
					{
						correctResolutionPercentage = resolutionPercentage == 100;
						super.state(context, correctResolutionPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage.message");
					}
					{
						correctResolution = resolution != null;
						super.state(context, correctResolution, "resolution", "acme.validation.trackingLog.resolution-notNull.message");
						correctResolution = !resolution.trim().equals("");
						super.state(context, correctResolution, "resolution", "acme.validation.trackingLog.resolution-notBlanck.message");
					}

				}
			}

		}

		result = !super.hasErrors(context);

		return result;
	}
}
