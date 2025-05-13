
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMRDeleteService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMRRepository repository;


	@Override
	public void authorise() {
		boolean authorised;
		int id = super.getRequest().getData("id", int.class);
		Collection<Aircraft> aircrafts;
		int aircraftId;
		Aircraft a;
		MaintenanceRecord mr;
		Technician loggedTechnician;

		authorised = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		if (super.getRequest().hasData("id", int.class)) {
			mr = this.repository.findMRById(id);
			loggedTechnician = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId());
			aircrafts = this.repository.findAllAircrafts();
			aircraftId = super.getRequest().getData("aircraft", int.class);
			a = this.repository.findAircraftById(aircraftId);

			if (aircraftId != 0)
				authorised = mr != null && super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr.getTechnician().getId() == loggedTechnician.getId() && mr.getDraftMode() && aircrafts.contains(a);
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMRById(id);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord mr) {
		super.bindObject(mr, "momentDone", "maintenanceStatus", "nextInspection", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final MaintenanceRecord mr) {
		Collection<Involves> relatedInvolves = this.repository.findInvolvesByMRId(mr.getId());

		this.repository.deleteAll(relatedInvolves);
		this.repository.delete(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {
		Dataset dataset;
		Collection<Technician> technicians;
		Collection<Aircraft> aircrafts;
		SelectChoices technicianChoices;
		SelectChoices aircraftChoices;
		SelectChoices maintenanceStatus;

		technicians = List.of(this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId()));
		aircrafts = this.repository.findAllAircrafts();
		maintenanceStatus = SelectChoices.from(MaintenanceStatus.class, mr.getMaintenanceStatus());
		aircraftChoices = SelectChoices.from(aircrafts, "model", mr.getAircraft());
		technicianChoices = SelectChoices.from(technicians, "licenseNumber", mr.getTechnician());

		dataset = super.unbindObject(mr, "momentDone", "maintenanceStatus", "nextInspection", "estimatedCost", "notes", "draftMode", "aircraft", "technician");
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("technicians", technicianChoices);
		dataset.put("status", maintenanceStatus);

		super.getResponse().addData(dataset);
	}

}
