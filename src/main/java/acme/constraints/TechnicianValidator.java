
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.realms.Technician;
import acme.realms.TechnicianRepository;

public class TechnicianValidator extends AbstractValidator<ValidTechnician, Technician> {

	@Autowired
	public TechnicianRepository repository;


	@Override
	protected void initialise(final ValidTechnician annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Technician t, final ConstraintValidatorContext context) {

		assert context != null;
		boolean res = false;

		if (t == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean uniqueLicenseNumber;
			List<String> allLicenseNumbers = this.repository.findAllLicenseNumbers();
			String licenseNumber = t.getLicenseNumber();

			if (allLicenseNumbers != null && licenseNumber != null) {
				uniqueLicenseNumber = allLicenseNumbers.stream().filter(ln -> ln.equals(licenseNumber)).toList().size() == 1;

				super.state(context, uniqueLicenseNumber, "licenseNumber", "acme.validation.technician.uniqueLicenseNumber.message");

			}
		}

		res = !super.hasErrors(context);
		return res;
	}
}
