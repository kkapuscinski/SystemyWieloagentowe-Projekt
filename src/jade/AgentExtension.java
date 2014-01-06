/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jade;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Karol
 */
public class AgentExtension {
    
    public static void DFRegister(Agent agent, String agentType)
    {
        DFAgentDescription template = new DFAgentDescription();
        template.setName( agent.getAID() ); 
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(agentType);
        sd.setName( agent.getName() );
        template.addServices(sd);
        try
        {
            DFService.register(agent, template );  
        } 
        catch (FIPAException fe) 
        { 
            fe.printStackTrace(); 
        }	
    }
    
    public static List<AID> findAgentsByType(Agent agent, String agentType)
	{
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(agentType);
            template.addServices(sd);
            ArrayList<AID> sellersAID = new ArrayList<>();
            try {
        	DFAgentDescription[] result = DFService.search(agent, template); 
        	for (int i = 0; i < result.length; i++) 
                {
                    sellersAID.add(result[i].getName());
                } 
            }
                catch (FIPAException fe) {
                fe.printStackTrace();
            }
            return sellersAID;
	}
}
