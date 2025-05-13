
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMRCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMRRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Collection<Aircraft> aircrafts;
		int aircraftId;
		Aircraft a;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		if (super.getRequest().hasData("id", int.class)) {
			aircrafts = this.repository.findAllAircrafts();
			aircraftId = super.getRequest().getData("aircraft", int.class);
			a = this.repository.findAircraftById(aircraftId);
			if (aircraftId != 0)
				status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && aircrafts.contains(a);
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord mr;
		Technician t = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId());

		mr = new MaintenanceRecord();
		mr.setDraftMode(true);
		mr.setTechnician(t);
		super.getBuffer().addData(mr);
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

		boolean validStatus;
		validStatus = super.getRequest().getData("maintenanceStatus", MaintenanceStatus.class) == MaintenanceStatus.COMPLETED;
		super.state(!validStatus, "maintenanceStatus", "acme.validation.technician.maintenanceRecord.error.validStatus.message");
	}

	@Override
	public void perform(final MaintenanceRecord mr) {
		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {
		assert mr != null;
		Dataset dataset;
		Collection<Aircraft> aircrafts;
		Collection<Technician> technicians;
		SelectChoices maintenanceStatus;
		SelectChoices aircraftChoices;
		SelectChoices technicianChoices;

		maintenanceStatus = SelectChoices.from(MaintenanceStatus.class, mr.getMaintenanceStatus());
		aircrafts = this.repository.findAllAircrafts();
		technicians = List.of(this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId()));
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
