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
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

// zachowanie przyjmujące oferty sprzedaży
class AcceptOffersBehaviourBroker extends SimpleBehaviour
{
    private AgentBroker myAgent; // agent zachowania
    private int State; // stan maszyny stanowej
    private Date endDate; // data zakończenia przyjmowania ofert sprzedaży
    private MessageTemplate mt; //  wzór odpowiedzi na zapytanie CFP

    AcceptOffersBehaviourBroker(AgentBroker agentBroker) 
    {
        myAgent = agentBroker;
        State = 0;
        endDate = new Date(new Date().getTime() + (1000 * 10)); // kiedy kończymy przyjmować oferty. 10 sekund
    }

    @Override
    public void action() {
        switch(State)
            {
                case(0):
                    // wysyłamy CFP do sprzedających o propozycje sprzedaży
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
                    // jeśli koniec składania ofert to kończymy zachowanie
                    if(endDate.after(new Date()))
                    {
                        //wpp sprawdzamy czy jest jakaś oferta
                        ACLMessage reply = myAgent.receive(mt);
                        if (reply != null) 
                        {
                            // Reply received
                            if (reply.getPerformative() == ACLMessage.PROPOSE) 
                            {
                                try 
                                {
                                    myAgent.Auctions.add(new BrokerAuction((AuctionParameters) reply.getContentObject(), myAgent.Auctions.size()));
                                    ACLMessage reply2 = reply.createReply();
                                    reply2.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                    reply2.setConversationId("auction-proposal");
                                    reply2.setContentObject(reply.getContentObject());
                                    myAgent.send(reply2);
                                } 
                                catch (UnreadableException ex) 
                                {
                                    Logger.getLogger(AcceptOffersBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
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
            System.out.println("Broker-agent "+myAgent.getAID().getName()+" wiating for sell proposal has ended. Starting auctions");
            myAgent.addBehaviour(new AuctionManagerBehaviourBroker(myAgent));
            return true;
        }
        else
        {
            return false;
        }
    }
}
