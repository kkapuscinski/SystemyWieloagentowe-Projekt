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
    private BrokerAuction ActiveAuction;
    private MessageTemplate mt;
    private int NoPropositionOffers;
    

    DutchAuctionBehaviourBroker(AgentBroker agent, BrokerAuction activeAuction, long period)
    {
        super(agent, period);
        myAgent = agent;
        State = 0;
        NoPropositionOffers = 0;
        ActiveAuction = activeAuction;
        ActiveAuction.ActualBid = new Bid(ActiveAuction.AuctionParameters.AuctionMinimalPrice, null);
        
    }

    @Override
    protected void onTick() {
        switch(State)
        {
            case(0):
                // wysyłamy CFP do kupujących
                mt = myAgent.sendAuctionCFPToBuyers(ActiveAuction);
                State++;
                break;
            case(1):
                // odbieramy propozycje kupna
                ACLMessage reply = myAgent.receive(mt);
                
                // jeśli jest brak ofert to zmniejszamy wartość aukcji i wysyłamy aktualną propozycję lub kończymy auckję jeśli przekroczono wartość minimalną
                if(reply == null)
                {
                    
                    if(ActiveAuction.ActualBid.Value > ActiveAuction.AuctionParameters.AuctionMinimalPrice)
                    {
                        ActiveAuction.ActualBid.Value = ActiveAuction.ActualBid.Value - ActiveAuction.AuctionParameters.MinimalStep;
                        myAgent.sendAuctionActualBidToBuyers(ActiveAuction);
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
                        ActiveAuction.ActualBid.Bidder = reply.getSender();
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
                if(ActiveAuction.ActualBid.Value > ActiveAuction.AuctionParameters.AuctionMinimalPrice)
                {
                    ActiveAuction.AuctionState = BrokerAuctionState.Sold;
                    myAgent.sendAuctionSoldEnd(ActiveAuction);
                }
                else
                {
                    ActiveAuction.AuctionState = BrokerAuctionState.NotSold;
                    myAgent.sendAuctionNotSoldEnd(ActiveAuction);
                }
                
                this.stop();
                
                break;
        }
    }

    
}
