
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_management.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightCreateService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight object = new Flight();
		object.setDraftMode(true);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Flight object) {
		assert object != null;
		super.bindObject(object, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight object) {
		assert object != null;
		// Puedes dejarlo vacío por ahora
	}

	@Override
	public void perform(final Flight object) {
		assert object != null;

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int managerId = this.repository.findManagerByUserAccountId(userAccountId);

		Manager manager = new Manager();
		manager.setId(managerId);

		object.setManager(manager);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Flight object) {
		Dataset dataset = super.unbindObject(object, "tag", "selfTransfer", "cost", "description");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
