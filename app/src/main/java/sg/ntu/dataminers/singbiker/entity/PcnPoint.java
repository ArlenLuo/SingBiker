package sg.ntu.dataminers.singbiker.entity;

import java.io.Serializable;

public class PcnPoint implements Comparable<PcnPoint>,Serializable{
	public int id;
	public Point ll;
	public double diff;
	public String name;
	@Override
	public String toString() {
		return "PcnPoint [id=" + id + ", ll=" + ll + ", diff=" + diff + ", name=" + name + "]";
	}
	public int compareTo(PcnPoint other){
		if(this.diff>other.diff)
			return 1;
		else if(this.diff<other.diff)
			return -1;
		else 
			return 0;
	}
	

}
