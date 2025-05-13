
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.realms.Technician;

@GuiController
public class TechnicianMRController extends AbstractGuiController<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMRListService			listService;

	@Autowired
	private TechnicianMRListPublicService	listPublicService;

	@Autowired
	private TechnicianMRShowService			showService;

	@Autowired
	private TechnicianMRCreateService		createService;

	@Autowired
	private TechnicianMRUpdateService		updateService;

	@Autowired
	private TechnicianMRDeleteService		deleteService;

	@Autowired
	private TechnicianMRPublishService		publishService;


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("list", this.listService);
		super.addCustomCommand("list-public", "list", this.listPublicService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
