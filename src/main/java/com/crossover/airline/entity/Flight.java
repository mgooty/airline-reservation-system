package com.crossover.airline.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "flight", uniqueConstraints = @UniqueConstraint(columnNames={"flight_code", "class"}))
public class Flight extends BaseEntity {

	private static final long serialVersionUID = -1775543068619893106L;

	public enum FlightStatus {
		SCHEDULED, DELAYED, CANCELLED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "flight_code", nullable = false)
	private String flightCode;
	
	@Column(name = "departure_date", nullable = false)
	private Date departureDate;
	
	@Column(name = "from_city", nullable = false)
	private String fromCity;
	
	@Column(name = "to_city", nullable = false)
	private String toCity;
	
	@Column(name = "duration_in_mins", nullable = false)
	private int durationInMins;
	
	@Column(name = "flight_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private FlightStatus flightStatus = FlightStatus.SCHEDULED;
	
	@Column(name = "class", nullable = false)
	@Enumerated(EnumType.STRING)
	private FlightClass flightClass;
	
	@Column(name = "total_no_of_seats", nullable = false)
	private int totalNoOfSeats;
	
	@Column(name = "no_of_seats_available", nullable = false)
	private int noOfSeatsAvailable;
	
	@Column(name = "no_of_seats_booked", nullable = true)
	private int noOfSeatsBooked;
	
	@Column(name = "price_per_seat", nullable = false)
	private Double pricePerSeat;
	
	@Column(name  = "lockId", nullable = false)
	@Version
	private long lockId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFlightCode() {
		return flightCode;
	}

	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public int getDurationInMins() {
		return durationInMins;
	}

	public void setDurationInMins(int durationInMins) {
		this.durationInMins = durationInMins;
	}

	public FlightStatus getFlightStatus() {
		return flightStatus;
	}

	public void setFlightStatus(FlightStatus flightStatus) {
		this.flightStatus = flightStatus;
	}

	public FlightClass getFlightClass() {
		return flightClass;
	}

	public void setFlightClass(FlightClass flightClass) {
		this.flightClass = flightClass;
	}

	public int getTotalNoOfSeats() {
		return totalNoOfSeats;
	}

	public void setTotalNoOfSeats(int totalNoOfSeats) {
		this.totalNoOfSeats = totalNoOfSeats;
	}

	public int getNoOfSeatsAvailable() {
		return noOfSeatsAvailable;
	}

	public void setNoOfSeatsAvailable(int noOfSeatsAvailable) {
		this.noOfSeatsAvailable = noOfSeatsAvailable;
	}

	public int getNoOfSeatsBooked() {
		return noOfSeatsBooked;
	}

	public void setNoOfSeatsBooked(int noOfSeatsBooked) {
		this.noOfSeatsBooked = noOfSeatsBooked;
	}

	public Double getPricePerSeat() {
		return pricePerSeat;
	}

	public void setPricePerSeat(Double pricePerSeat) {
		this.pricePerSeat = pricePerSeat;
	}

	public long getLockId() {
		return lockId;
	}

	public void setLockId(long lockId) {
		this.lockId = lockId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((departureDate == null) ? 0 : departureDate.hashCode());
		result = prime * result + durationInMins;
		result = prime * result + ((flightClass == null) ? 0 : flightClass.hashCode());
		result = prime * result + ((flightCode == null) ? 0 : flightCode.hashCode());
		result = prime * result + ((flightStatus == null) ? 0 : flightStatus.hashCode());
		result = prime * result + ((fromCity == null) ? 0 : fromCity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (int) (lockId ^ (lockId >>> 32));
		result = prime * result + noOfSeatsAvailable;
		result = prime * result + noOfSeatsBooked;
		result = prime * result + ((pricePerSeat == null) ? 0 : pricePerSeat.hashCode());
		result = prime * result + ((toCity == null) ? 0 : toCity.hashCode());
		result = prime * result + totalNoOfSeats;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Flight other = (Flight) obj;
		if (departureDate == null) {
			if (other.departureDate != null)
				return false;
		} else if (!departureDate.equals(other.departureDate))
			return false;
		if (durationInMins != other.durationInMins)
			return false;
		if (flightClass != other.flightClass)
			return false;
		if (flightCode == null) {
			if (other.flightCode != null)
				return false;
		} else if (!flightCode.equals(other.flightCode))
			return false;
		if (flightStatus != other.flightStatus)
			return false;
		if (fromCity == null) {
			if (other.fromCity != null)
				return false;
		} else if (!fromCity.equals(other.fromCity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lockId != other.lockId)
			return false;
		if (noOfSeatsAvailable != other.noOfSeatsAvailable)
			return false;
		if (noOfSeatsBooked != other.noOfSeatsBooked)
			return false;
		if (pricePerSeat == null) {
			if (other.pricePerSeat != null)
				return false;
		} else if (!pricePerSeat.equals(other.pricePerSeat))
			return false;
		if (toCity == null) {
			if (other.toCity != null)
				return false;
		} else if (!toCity.equals(other.toCity))
			return false;
		if (totalNoOfSeats != other.totalNoOfSeats)
			return false;
		return true;
	}
	
	
}
