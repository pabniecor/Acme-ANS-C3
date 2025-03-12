
package acme.constraints;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.entities.maintenance_and_technical.ActivityLog;
import acme.entities.maintenance_and_technical.ActivityLogRepository;

public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog value, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (value == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			List<ActivityLog> al = this.repository.findActivityLogtByFlightAssignmentId(value.getFlightAssignment().getId());
			ActivityLog al1 = al.get(0);
			Date legDate = al1.getFlightAssignment().getLeg().getScheduledArrival();

			boolean properDate = al1.getRegistrationMoment().after(legDate);

			super.state(context, properDate, "registrationMoment", "The registration moment is established before the leg arrive");
		}
		result = !super.hasErrors(context);
		return result;
	}

}
