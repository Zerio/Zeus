package com.elfadili.wheresmyplaces.object;

public class StepObject {
	private String distance;
	private String duration;
	private String description;
	private String travelMode;
	//private Location startLocation;
	//private Location endLocation;

	public StepObject() {
		super();
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTravelMode() {
		return travelMode;
	}

	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}

//	public Location getStartLocation() {
//		return startLocation;
//	}
//
//	public void setStartLocation(Location startLocation) {
//		this.startLocation = startLocation;
//	}
//
//	public Location getEndLocation() {
//		return endLocation;
//	}
//
//	public void setEndLocation(Location endLocation) {
//		this.endLocation = endLocation;
//	}

}
