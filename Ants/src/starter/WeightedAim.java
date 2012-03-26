package starter;

public class WeightedAim implements Comparable<WeightedAim> {

	private int weight;
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Aim getAim() {
		return aim;
	}

	public void setAim(Aim aim) {
		this.aim = aim;
	}

	private Aim aim;

	public WeightedAim(int weigth, Aim aim){
		this.weight = weigth;
		this.aim = aim;
	}

	@Override
	public int compareTo(WeightedAim o) {
			return weight - o.getWeight();
	}

	@Override
	public String toString() {
		return "WeightedAim [weight=" + weight + ", aim=" + aim + "]";
	}
	
}
