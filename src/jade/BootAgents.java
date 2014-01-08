package jade;

import java.util.ArrayList;
import java.util.List;
import jade.Boot3;
import Seller.AgentSeller;

public class BootAgents {

	public static void main(String args[]) {

		 List<String> bootAgents = new ArrayList<String>(0);
		 bootAgents.add("-gui");
                 bootAgents.add("Seller:Seller.AgentSeller()");
		 bootAgents.add("Seller2:Seller.AgentSeller()");
                 //bootAgents.add("Seller3:Seller.AgentSeller()");
                 //bootAgents.add("Seller4:Seller.AgentSeller()");
                 bootAgents.add("Broker:Broker.AgentBroker()");
                 bootAgents.add("Buyer:Buyer.AgentBuyer(1000 0.07)");
                 bootAgents.add("Buyer2:Buyer.AgentBuyer(1000 0.05)");
                 bootAgents.add("Buyer3:Buyer.AgentBuyer(1000 0.02)");
                 bootAgents.add("Buyer4:Buyer.AgentBuyer(1000 0.1)");
                 bootAgents.add("Buyer5:Buyer.AgentBuyer(1000 0.03)");
		
		 String[] strarray = bootAgents.toArray(new String[0]);
		 new Boot3(strarray);
		}
	}

