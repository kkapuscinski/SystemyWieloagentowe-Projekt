package Buyer;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class AgentBuyer extends Agent {
	private String targetBookTitle;
	
	protected void setup() 
        {
            
        }

        protected void takeDown() 
        {
            System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
        }
	
}
