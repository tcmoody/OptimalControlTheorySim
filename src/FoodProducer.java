
public class FoodProducer implements Producer {
	private Resource resource;
	private int goods;
	private int price;
	private int profit;
	private int productionCosts;
	private int numGoodsSold;

	public FoodProducer(Resource resource) {
		this.resource = resource;
		profit = 0;
		goods = 0;
		productionCosts = 2;
		price = 10;
		numGoodsSold = 0;
	}

	public int getNumGoodsSold() {
		return numGoodsSold;
	}

	@Override
	public int getProducedGoods() {
		return goods;
	}

	public int getProfit(){
		return profit;
	}
	
	@Override
	public void calculateProfit() {
		this.profit += (numGoodsSold * price) - (productionCosts * goods);
	}

	@Override
	public void produce(int amountToProduce) {
		for (int i = 0; i < amountToProduce; i++) {
			goods++;
		}
	}

	@Override
	public void sellGoods(int numSold) {
		numGoodsSold += numSold;
		goods -= numSold;
	}

}
