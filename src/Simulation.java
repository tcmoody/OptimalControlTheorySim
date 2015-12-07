import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Simulation {
	private ArrayList<Consumer> consumers;
	private int numInitialConsumers;
	private FoodProducer foodCart;
	private WeaponProducer weaponMerchant;
	private Land land;
	private Wood wood;
	private int timePeriods;
	private int currentTimePeriod;
	private int optimalConstant;
	private int previousState;

	public Simulation(int numConsumers, int timePeriods) {
		consumers = new ArrayList<Consumer>();
		for (int i = 0; i < numConsumers; i++) {
			consumers.add(new Consumer());
		}
		numInitialConsumers = numConsumers;
		land = new Land(numConsumers);
		wood = new Wood(numConsumers);
		foodCart = new FoodProducer(land);
		weaponMerchant = new WeaponProducer(wood);
		this.timePeriods = timePeriods;
		optimalConstant = weaponMerchant.getResource().getNumResources();
		currentTimePeriod = 0;
	}

	/**
	 * returns the end state of the resource owned by the producer in that time
	 * period
	 * 
	 * @return state
	 */
	public int optimalControlLaw() {
		int state;
		int temp;
		if (currentTimePeriod == 1) {
			state = (int) (10 * (numInitialConsumers * (Math.pow((double) -currentTimePeriod, 3.0) / 3))
					+ optimalConstant);
			previousState = state;
			return state;
		} else {
			state = (int) (numInitialConsumers * (Math.pow((double) -currentTimePeriod, 3.0) / 3) + optimalConstant);
			temp = state;
			state = previousState + state;
			previousState = temp;
			return state;
		}
	}

	/**
	 * returns the currentTimePeriod
	 * 
	 * @return currentTimePeriod
	 */
	public int getCurrentTimePeriod() {
		return currentTimePeriod;
	}

	/**
	 * sets the currentTimePeriod
	 * 
	 * @param currentTimePeriod
	 */
	public void setCurrentTimePeriod(int currentTimePeriod) {
		this.currentTimePeriod = currentTimePeriod;
	}

	/**
	 * generates each consumers wallets for the time period
	 */
	public void generateDollaDollaBills() {
		for (Consumer c : consumers) {
			c.dollaDollaBillYall();
		}
	}

	/**
	 * tells each producer to produce
	 */
	public void produceGoods(int amountToProduce) {
		weaponMerchant.produce(amountToProduce);
	}

	/**
	 * consumers buy weapons, performing greedy purchases right now. Consumers
	 * will buy until they can't afford any more
	 */
	public void letsGoShopping() {
		for (Consumer c : consumers) {
			int potentialPurchase = 0;
			int lastWeapons;
			while (c.getMoney() > potentialPurchase * weaponMerchant.getPrice()) {
				potentialPurchase++;
			}
			if (weaponMerchant.getProducedGoods() - weaponMerchant.getNumGoodsSold() > potentialPurchase) {
				c.purchaseWeapons(potentialPurchase, weaponMerchant.getPrice());
				weaponMerchant.sellGoods(potentialPurchase);
			} else {
				lastWeapons = weaponMerchant.getProducedGoods() - weaponMerchant.getNumGoodsSold();
				c.purchaseWeapons(lastWeapons, weaponMerchant.getPrice());
				weaponMerchant.sellGoods(lastWeapons);
				return;
			}
		}
		weaponMerchant.calculateProfit();
		weaponMerchant.setNumGoodsSold(0);
	}

	/**
	 * consumers trade between each other, so far only set for weapons
	 */
	public void heyYaWannaTrade() {
		ArrayList<Consumer> buyers = new ArrayList<Consumer>();
		ArrayList<Consumer> sellers = new ArrayList<Consumer>();
		for (Consumer c : consumers) {
			if (c.getNumWeapons() < 4) {
				buyers.add(c);
			} else if (c.getNumWeapons() > 4) {
				sellers.add(c);
			}
		}

		consumers.removeAll(buyers);
		consumers.removeAll(sellers);

		for (Consumer c : sellers) {
			for (Consumer f : buyers) {
				if (f.getNumWeapons() == 4 || f.getMoney() < 10) {
					break;
				}
				if (c.getNumWeapons() < 4) {
					break;
				} else {
					c.setNumWeapons(c.getNumWeapons() - 1);
					c.setMoney(c.getMoney() + 10);
					f.setNumWeapons(f.getNumWeapons() + 1);
					f.setMoney(f.getMoney() - 10);
				}

			}
		}
		consumers.addAll(buyers);
		consumers.addAll(sellers);
	}

	/**
	 * generates the next generation of consumers, if they survive the
	 * realSurvivalRate vs random 0-1 another consumer is produced, if they
	 * don't then that consumer is removed(aka dies)
	 * Also destroys weapons at the end of the time period
	 */
	public void newGeneration() {
		Random rn = new Random();
		int newGeneration = 0;
		for (Iterator<Consumer> it = consumers.iterator(); it.hasNext();) {
			Consumer c = it.next();
			if (c.calculateRealSurvivalRate() > rn.nextDouble()) {
				newGeneration++;
			} else {
				it.remove();
			}
		}
		
		int broken;
		for(Consumer c: consumers){
			broken=0;
			int breakChance;
			for(int i = 0; i < c.getNumWeapons(); i++){
				breakChance = rn.nextInt(10);
				if(rn.nextDouble()>4){
					broken++;
				}
			}
			c.setNumWeapons(c.getNumWeapons()-broken);
		}
		
		for (int i = 0; i < newGeneration; i++) {
			consumers.add(new Consumer());
		}
	}

	public ArrayList<Consumer> getConsumers() {
		return consumers;
	}

	public int getNumConsumers() {
		return consumers.size();
	}

	public FoodProducer getFoodCart() {
		return foodCart;
	}

	public WeaponProducer getWeaponMerchant() {
		return weaponMerchant;
	}

}
