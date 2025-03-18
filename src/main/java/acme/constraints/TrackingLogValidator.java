
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
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
			Boolean indicator = trackingLog.getIndicator();

			if (indicator != null) {
				Double resolutionPercentage = trackingLog.getResolutionPercentage();
				String resolution = trackingLog.getResolution();
				boolean correctResolutionPercentage;
				boolean correctResolution;

				if (indicator) {
					correctResolutionPercentage = resolutionPercentage != null;
					super.state(context, correctResolutionPercentage, "resolutionPercentage", "Resolution percentage cannot be null");
				} else {
					correctResolutionPercentage = resolutionPercentage == null;
					super.state(context, correctResolutionPercentage, "resolutionPercentage", "Resolution percentage must be null");
				}

				correctResolution = resolution != null;
				super.state(context, correctResolution, "resolution", "Resolution cannot be null");
			}

		}

		result = !super.hasErrors(context);

		return result;
	}
}
