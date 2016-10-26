package sg.ntu.dataminers.singbiker.entity;

public class Incident {
	Point location;
	String type;
	String description;
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String toString(){
		return type+"\t"+description+"\t"+location;
	}

	
}
