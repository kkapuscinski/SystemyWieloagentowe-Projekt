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

// zachowanie brokera dla aukcji vickereya
class VickereyAuctionBehaviourBroker extends TickerBehaviour {

    private AgentBroker myAgent;
    private int State;
    private MessageTemplate mt;
    private int NoPropositionOffers; // brak ofert przez określony czas
    
    VickereyAuctionBehaviourBroker(AgentBroker agent,  long period)
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
                // odbieramy propozycje kupna 
                ACLMessage reply = myAgent.receive(mt);
                
                // jeśli brak ofert liczymy do 5 sekund i kończymy aukcję
                if(reply == null)
                {
                    NoPropositionOffers++;
                    if(NoPropositionOffers > 5) State++;
                    return;
                }
                // odbieramy propozycje i ustawiamy najwyższy bid oraz drugi w kolejności
                while (reply != null) {                    
                    // Reply received
                    if (reply.getPerformative() == ACLMessage.PROPOSE) 
                    {
                        try 
                        {
                            Bid tmpbid = (Bid) reply.getContentObject();
                            if( (myAgent.ActiveAuction.HighestBid == null || tmpbid.Value > myAgent.ActiveAuction.HighestBid.Value))
                            {
                                if(myAgent.ActiveAuction.HighestBid == null)
                                {
                                    myAgent.ActiveAuction.SecondHighestBid = tmpbid;
                                }
                                else
                                {
                                    myAgent.ActiveAuction.SecondHighestBid = myAgent.ActiveAuction.HighestBid;
                                }
                                
                                myAgent.ActiveAuction.HighestBid = tmpbid;
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
