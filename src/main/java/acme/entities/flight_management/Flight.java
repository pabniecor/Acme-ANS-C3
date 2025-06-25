
package acme.entities.flight_management;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "draftMode")
})
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50) //min especifies "up to 50 characters"
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney(min = 0.00, max = 1000000.00)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				draftMode;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Date getDeparture() {
		Date result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findLegsOrderByAscendent(this.getId()).stream().findFirst().orElse(null);
		result = leg != null ? leg.getScheduledDeparture() : null;
		return result;
	}

	@Transient
	public Date getArrival() {
		Date result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findLegsOrderByDescendent(this.getId()).stream().findFirst().orElse(null);
		result = leg != null ? leg.getScheduledArrival() : null;
		return result;
	}

	@Transient
	public String getOriginCity() {
		String result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findLegsOrderByAscendent(this.getId()).stream().findFirst().orElse(null);
		result = leg != null ? leg.getDepartureAirport().getCity() : null;
		return result;
	}

	@Transient
	public String getDestinationCity() {
		String result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findLegsOrderByDescendent(this.getId()).stream().findFirst().orElse(null);
		result = leg != null ? leg.getArrivalAirport().getCity() : null;
		return result;
	}

	@Transient
	public Integer getLayovers() {
		Integer result;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		result = legRepository.countNumberOfLegsOfFlight(this.getId());
		return result;
	}

	@Transient
	public String getBookingFlight() {
		return this.getOriginCity() + "-->" + this.getDestinationCity();
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager manager;

}
