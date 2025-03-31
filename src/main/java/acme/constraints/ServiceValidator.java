
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.components.ServiceRepository;
import acme.entities.airport_management.Service;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ServiceRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (service == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Integer currentYear = MomentHelper.getCurrentMoment().getYear(); // Difference between current year and 1900
			String currentYearToString = currentYear.toString();
			String promotionCode = service.getPromotionCode();

			boolean correctPromotionCode = currentYearToString.charAt(currentYearToString.length() - 2) == promotionCode.charAt(promotionCode.length() - 2)
				&& currentYearToString.charAt(currentYearToString.length() - 1) == promotionCode.charAt(promotionCode.length() - 1);

			super.state(context, correctPromotionCode, "promotionCode", "acme.validation.service.promotionCode.message");
		}
		result = !super.hasErrors(context);
		return result;
	}
}
