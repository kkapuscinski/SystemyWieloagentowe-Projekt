/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.Bid;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karol
 */
class DutchAuctionBehaviourBroker extends TickerBehaviour {

    private AgentBroker myAgent;
    private int State;
    private MessageTemplate mt;
    private int NoPropositionOffers;
    

    DutchAuctionBehaviourBroker(AgentBroker agent, long period)
    {
        super(agent, period);
        myAgent = agent;
        State = 0;
        NoPropositionOffers = 0;
        myAgent.ActiveAuction.ActualBid = new Bid(myAgent.ActiveAuction.AuctionParameters.DutchAuctionStartingPrice, null);
        
    }

    @Override
    protected void onTick() {
        switch(State)
        {
            case(0):
                // wysyłamy CFP do kupujących
                mt = myAgent.sendAuctionCFPToBuyers(myAgent.ActiveAuction);
                State++;
                break;
            case(1):
                // odbieramy propozycje kupna
                ACLMessage reply = myAgent.receive(mt);
                
                // jeśli jest brak ofert to zmniejszamy wartość aukcji i wysyłamy aktualną propozycję lub kończymy auckję jeśli przekroczono wartość minimalną
                if(reply == null)
                {
                    
                    if(myAgent.ActiveAuction.ActualBid.Value > myAgent.ActiveAuction.AuctionParameters.AuctionMinimalPrice)
                    {
                        myAgent.ActiveAuction.ActualBid.Value = myAgent.ActiveAuction.ActualBid.Value - (myAgent.ActiveAuction.AuctionParameters.MinimalStep * myAgent.ActiveAuction.AuctionParameters.Amount);
                        myAgent.sendAuctionActualBidToBuyers(myAgent.ActiveAuction);
                    }
                    else
                    {
                        State++;
                        // aukcja kończy się niepomyślnie
                    }
                    
                    return;
                }
                else
                {
                    if (reply.getPerformative() == ACLMessage.PROPOSE) 
                    {
                        myAgent.ActiveAuction.ActualBid.Bidder = reply.getSender();
                        State++;
                    }
                    else
                    {
                        //nieznany komunikat
                    }
                }
                break;
            case(2):
                // koniec aukcji wysyłamy informację do wszystkich o zakończeniu aukcji, informacja do zwycięzcy(jeśli był) oraz do sprzedającego
                if(myAgent.ActiveAuction.ActualBid.Value > myAgent.ActiveAuction.AuctionParameters.AuctionMinimalPrice)
                {
                    myAgent.ActiveAuction.AuctionState = BrokerAuctionState.Sold;
                    myAgent.sendAuctionSoldEnd(myAgent.ActiveAuction);
                }
                else
                {
                    myAgent.ActiveAuction.AuctionState = BrokerAuctionState.NotSold;
                    myAgent.sendAuctionNotSoldEnd(myAgent.ActiveAuction);
                }
                
                this.stop();
                
                break;
        }
    }

    
}
