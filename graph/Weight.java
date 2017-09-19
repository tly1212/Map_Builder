package cs311.hw8.graph;

import cs311.hw8.graphalgorithms.IWeight;

public class Weight implements IWeight{

	double wei;
	public Weight(int weight){
		wei = (double)weight;
	}
	@Override
	public double getWeight() {
		// TODO Auto-generated method stub
		return wei;
	}

}
