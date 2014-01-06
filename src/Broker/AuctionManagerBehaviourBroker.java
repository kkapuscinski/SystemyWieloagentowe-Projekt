/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.AuctionType;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karol
 */
class AuctionManagerBehaviourBroker extends SimpleBehaviour 
{
    private AgentBroker myAgent;
    private int State;
    private BrokerAuction ActiveAuction;
    
    
    
    public AuctionManagerBehaviourBroker(AgentBroker agent) 
    {
        myAgent = agent;
        State = 0;
        
        
    }

    @Override
    public void action() 
    {
        switch(State)
        {
            case(0):
                // wybieramy pierwszą nie zakończoną aukcję i uruchamiamy odpowiedni beahviurAukcji
                boolean AllAcuctionsHasEnded = true;
                for (int i = 0; i < myAgent.Auctions.size(); i++) {
                    if(!(myAgent.Auctions.get(i).AuctionState == BrokerAuctionState.Sold || myAgent.Auctions.get(i).AuctionState == BrokerAuctionState.NotSold))
                    {
                        ActiveAuction = myAgent.Auctions.get(i);
                        AllAcuctionsHasEnded = false;
                        break;
                    }
                }
                if (AllAcuctionsHasEnded)
                {
                    State = 2;
                }
                
                switch(ActiveAuction.Auction.AuctionType)
                {
                    case Dutch:
                        myAgent.addBehaviour(new DutchAuctionBehaviourBroker(myAgent, ActiveAuction, 1000));
                        break;
                        
                    case English:
                        myAgent.addBehaviour(new EnglishAuctionBehaviourBroker(myAgent, ActiveAuction, 1000));
                        break;
                    
                    case Vikerey:
                        myAgent.addBehaviour(new VickereyAuctionBehaviourBroker(myAgent, ActiveAuction, 1000));
                        break;
                        
                }
                State++;
                
                break;
            case(1):
                if((ActiveAuction.AuctionState == BrokerAuctionState.Sold || ActiveAuction.AuctionState == BrokerAuctionState.NotSold))
                {
                    State--;
                }
                break;
        }
        
    }

    @Override
    public boolean done() {
        if(State == 2)
        {
            System.out.println("Broker-agent "+myAgent.getAID().getName()+" All auctions had ended.");
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
