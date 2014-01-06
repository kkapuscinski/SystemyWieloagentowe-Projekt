/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.AuctionParameters;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karol
 */
class AcceptOffersBehaviourBroker extends SimpleBehaviour
{
    private AgentBroker myAgent;
    private int State;
    private Date endDate;
    private MessageTemplate mt;

    AcceptOffersBehaviourBroker(AgentBroker agentBroker) 
    {
        myAgent = agentBroker;
        State = 0;
        endDate = new Date(new Date().getTime() + (1000 * 30)); // kiedy kończymy przyjmować oferty
    }

    @Override
    public void action() {
        switch(State)
            {
                case(0):
                    ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < myAgent.Sellers.size(); ++i) {
                        msg.addReceiver(myAgent.Sellers.get(i));
                    }
                    msg.setContent("Waiting for action proposals");
                    msg.setConversationId("auction-proposal");
                    msg.setReplyWith("msg"+System.currentTimeMillis()); // Unique value
                    myAgent.send(msg);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("auction-proposal"),
                    MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
                    State++;
                    break;
                    
                case(1):
                    if(endDate.after(new Date()))
                    {
                        ACLMessage reply = myAgent.receive(mt);
                        if (reply != null) 
                        {
                            // Reply received
                            if (reply.getPerformative() == ACLMessage.PROPOSE) 
                            {
                                try 
                                {
                                    myAgent.Auctions.add(new BrokerAuction((AuctionParameters) reply.getContentObject(), myAgent.Auctions.size()));
                                } 
                                catch (UnreadableException ex) 
                                {
                                    Logger.getLogger(AcceptOffersBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            else
                            {
                                // nieznane wysłać wiadomość o nie prawidłowej formie
                            }
                        }
                    }
                    else
                    {
                        State++;
                    }
                    break;
            }
    }

    @Override
    public boolean done() 
    {
        if(State == 2)
        {
            System.out.println("Broker-agent "+myAgent.getAID().getName()+" wiating for proposal has ended.");
            myAgent.addBehaviour(new AuctionManagerBehaviourBroker(myAgent));
            return true;
        }
        else
        {
            return false;
        }
    }
}
