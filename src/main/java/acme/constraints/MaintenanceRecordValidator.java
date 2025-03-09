
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.maintenance_and_technical.MaintenanceRecord;

public class MaintenanceRecordValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecord> {

	//	As there's no repositories yet, this part stays commented
	//	public MaintenanceRecordRepository repository;

	@Override
	protected void initialise(final ValidMaintenanceRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecord mr, final ConstraintValidatorContext context) {

		assert context != null;
		boolean res = false;

		if (mr == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean correctNextInspection;
			Date momentDone = mr.getMomentDone();
			Date nextInspection = mr.getNextInspection();

			if (momentDone != null && nextInspection != null) {
				correctNextInspection = MomentHelper.isBefore(momentDone, nextInspection);

				super.state(context, correctNextInspection, "nextInspection", "acme.validation.maintenanceRecord.nextInspection.message");
			}
		}

		res = !super.hasErrors(context);

		return res;
	}

}
