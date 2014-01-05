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
                 bootAgents.add("Broker:Broker.AgentBroker()");
                 //bootAgents.add("Seller:AgentSeller(Władca Pierścieni)");
		 //bootAgents.add("Buyer:AgentBuyer(Hobbit)");
		
		 String[] strarray = bootAgents.toArray(new String[0]);
		 new Boot3(strarray);
		}
	}

