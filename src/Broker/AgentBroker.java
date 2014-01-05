/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.Auction;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Karol
 */
public class AgentBroker extends Agent {
    public List<BrokerAuction> Auctions;
    public Codec Codec = new SLCodec();
    
    protected void setup() 
    {
        Auctions = new ArrayList<BrokerAuction>();

        System.out.println("Hello! Broker-agent "+getAID().getName()+" is ready.");
        Object[] args = getArguments();
        getContentManager().registerLanguage(Codec);

        DFRegister("broker", this);
        
        AcceptOffersBehaviourBroker behaviourBroker = new AcceptOffersBehaviourBroker(this);
        addBehaviour(behaviourBroker);
    }

    protected void takeDown() 
    {
        System.out.println("Broker-agent "+getAID().getName()+" terminating.");
    }
		

    public static void DFRegister(String typAgenta, Agent agent)
    {
        DFAgentDescription template = new DFAgentDescription();
        template.setName( agent.getAID() ); 
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(typAgenta );
        sd.setName( agent.getName() );
        template.addServices(sd);

        try
        {
            DFService.register(agent, template );  
        } 
        catch (FIPAException fe) { fe.printStackTrace(); }	
    }
    
}
