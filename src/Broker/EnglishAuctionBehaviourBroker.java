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

// zachowanie brokera dla aukcji angielskiej
class EnglishAuctionBehaviourBroker extends TickerBehaviour {

    private AgentBroker myAgent;
    private int State;
    private MessageTemplate mt;
    private int NoPropositionOffers; // brak ofert przez określony czas
    

    EnglishAuctionBehaviourBroker(AgentBroker agent, long period)
    {
        super(agent, period);
        myAgent = agent;
        State = 0;
        NoPropositionOffers = 0;
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
                // odbieramy propozycje kupna i wysyłamy informacje o najwyższej aktualnej propozycji
                ACLMessage reply = myAgent.receive(mt);
                
                // jeśli jest brak to liczymy do 3 sekund i kończymy aukcję
                if(reply == null)
                {
                    NoPropositionOffers++;
                    if(NoPropositionOffers > 3) State++;
                    return;
                }
                //dla każdej propozycji sprawdzamy czy jest większa niż aktualna propozycja i ustawiamy jąjako najwyższą
                while (reply != null) {                    
                    // Reply received
                    if (reply.getPerformative() == ACLMessage.PROPOSE) 
                    {
                        try 
                        {
                            Bid tmpbid = (Bid) reply.getContentObject();
                            
                            if(myAgent.ActiveAuction.HighestBid != null)
                            {
                                if(tmpbid.Value > myAgent.ActiveAuction.HighestBid.Value && 
                                    (tmpbid.Value - myAgent.ActiveAuction.HighestBid.Value) > myAgent.ActiveAuction.Auction.MinimalStep)
                                {
                                    myAgent.ActiveAuction.HighestBid = tmpbid;
                                }
                            }
                            else
                            {
                                if(tmpbid.Value > myAgent.ActiveAuction.AuctionParameters.EnglishAuctionStartingPrice && 
                                    (tmpbid.Value - myAgent.ActiveAuction.AuctionParameters.EnglishAuctionStartingPrice) > myAgent.ActiveAuction.Auction.MinimalStep)
                                {
                                    myAgent.ActiveAuction.HighestBid = tmpbid;
                                }
                            }
                            
                        } 
                        catch (UnreadableException ex) {
                            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    reply = myAgent.receive(mt);
                }
                // wysyłamy informację do kupujących o najwyższym aktualnym bidzie
                myAgent.sendAuctionActualBidToBuyers(myAgent.ActiveAuction);
                
                break;
            case(2):
                // koniec aukcji wysyłamy informację do wszystkich o zakończeniu aukcji, informacja do zwycięzcy(jeśli był) oraz do sprzedającego
                if(myAgent.ActiveAuction.HighestBid != null && myAgent.ActiveAuction.HighestBid.Value > myAgent.ActiveAuction.AuctionParameters.AuctionMinimalPrice)
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
