
package acme.features.technician.maintenanceRecord;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.MaintenanceStatus;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMRPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMRRepository repository;


	@Override
	public void authorise() {
		Collection<Aircraft> aircrafts;
		int aircraftId;
		Aircraft a;
		boolean authorised;

		authorised = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		if (super.getRequest().getMethod().equals("GET"))
			authorised = false;
		else {
			aircrafts = this.repository.findAllAircrafts();
			aircraftId = super.getRequest().getData("aircraft", int.class);
			a = this.repository.findAircraftById(aircraftId);

			if (aircraftId != 0)
				authorised = aircrafts.contains(a);
			else
				authorised = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		MaintenanceRecord mr;
		int id;

		id = super.getRequest().getData("id", int.class);
		mr = this.repository.findMRById(id);

		super.getBuffer().addData(mr);
	}

	@Override
	public void bind(final MaintenanceRecord mr) {
		super.bindObject(mr, "momentDone", "maintenanceStatus", "nextInspection", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		boolean hasNoTasks;
		boolean confirmation;
		Collection<Involves> involves;
		Collection<Task> involvedTasks = new ArrayList<>();

		involves = this.repository.findInvolvesByMRId(mr.getId());
		for (Involves i : involves) {
			Task t = this.repository.findTaskById(i.getTask().getId());
			involvedTasks.add(t);
		}

		hasNoTasks = involvedTasks.isEmpty();
		super.state(!hasNoTasks, "draftMode", "acme.validation.technician.maintenanceRecord.error.noMrWithoutTasks.message");

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final MaintenanceRecord mr) {
		mr.setDraftMode(false);
		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

		Dataset dataset;
		SelectChoices maintenanceStatus;
		Collection<Aircraft> aircrafts;
		Collection<Technician> technicians;
		SelectChoices aircraftChoices;
		SelectChoices technicianChoices;

		maintenanceStatus = SelectChoices.from(MaintenanceStatus.class, mr.getMaintenanceStatus());
		aircrafts = this.repository.findAllAircrafts();
		technicians = this.repository.findAllTechnicians();
		aircraftChoices = SelectChoices.from(aircrafts, "model", mr.getAircraft());
		technicianChoices = SelectChoices.from(technicians, "licenseNumber", mr.getTechnician());

		dataset = super.unbindObject(mr, "momentDone", "maintenanceStatus", "nextInspection", "estimatedCost", "notes", "draftMode", "aircraft", "technician");
		dataset.put("status", maintenanceStatus);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("technicians", technicianChoices);

		super.getResponse().addData(dataset);
	}

}
