/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.Bid;
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
class EnglishAuctionBehaviourBroker extends TickerBehaviour {

    private AgentBroker myAgent;
    private int State;
    private BrokerAuction ActiveAuction;
    private MessageTemplate mt;
    private int NoPropositionOffers;
    

    EnglishAuctionBehaviourBroker(AgentBroker agent, BrokerAuction activeAuction, long period)
    {
        super(agent, period);
        myAgent = agent;
        State = 0;
        NoPropositionOffers = 0;
        ActiveAuction = activeAuction;
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
                // odbieramy propozycje kupna i wysyłamy informacje o najwyższej aktualnej propozycji
                ACLMessage reply = myAgent.receive(mt);
                
                // jeśli jest brak to liczymy do 3 sekund i kończymy aukcję
                if(reply == null)
                {
                    NoPropositionOffers++;
                    if(NoPropositionOffers > 3) State++;
                    return;
                }
                
                while (reply != null) {                    
                    // Reply received
                    if (reply.getPerformative() == ACLMessage.PROPOSE) 
                    {
                        try 
                        {
                            Bid tmpbid = (Bid) reply.getContentObject();
                            if( (ActiveAuction.HighestBid == null || tmpbid.Value > ActiveAuction.HighestBid.Value) && (tmpbid.Value - ActiveAuction.HighestBid.Value) > ActiveAuction.Auction.MinimalStep)
                            {
                                ActiveAuction.HighestBid = tmpbid;
                            }
                        } 
                        catch (UnreadableException ex) {
                            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else
                    {
                        // nieznany komunikat
                    }
                    reply = myAgent.receive(mt);
                }
                
                myAgent.sendAuctionActualBidToBuyers(ActiveAuction);
                
                break;
            case(2):
                // koniec aukcji wysyłamy informację do wszystkich o zakończeniu aukcji, informacja do zwycięzcy(jeśli był) oraz do sprzedającego
                if(ActiveAuction.HighestBid.Value > ActiveAuction.AuctionParameters.AuctionMinimalPrice)
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
