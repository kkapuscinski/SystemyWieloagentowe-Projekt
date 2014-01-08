/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Buyer;

import Seller.AgentSeller;
import jade.Bid;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kkapuscinski
 */
class VikereyAuctionBehaviourBuyer extends SimpleBehaviour {
    private AgentBuyer myAgent;
    private int State;
    private Bid MyBid;
    private MessageTemplate mt;
    
    
    public VikereyAuctionBehaviourBuyer(AgentBuyer agent) {
        myAgent = agent;
        State = 0;
        mt =MessageTemplate.and(MessageTemplate.MatchConversationId("Auction-Buyers-Proposals"), MessageTemplate.MatchSender(myAgent.ActiveauctionCfpMsg.getSender()));
        
    }

    @Override
    public void action() {
        switch(State)
        {
            case(0):
                // wysyłamy oferte zgodnie z dostepnym budzetem, potrzebami i współczynnikiem agresywności
                float tmpBidValue = myAgent.ProductsRealValue.get(myAgent.ActiveAuction.Product) * myAgent.ActiveAuction.Amount;
                tmpBidValue = tmpBidValue + tmpBidValue* (myAgent.Random.nextFloat()* myAgent.Aggressiveness);
                if(tmpBidValue > myAgent.Cash)
                {
                    tmpBidValue = myAgent.Cash;
                }
                MyBid = new Bid(tmpBidValue, myAgent.getAID());
                
                // wprowadzam losowe opóźnienie
                try {
                    Thread.sleep(myAgent.Random.nextInt(1000));
                } catch (InterruptedException ex) 
                {
                    Logger.getLogger(AgentSeller.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                myAgent.SendAuctionBid(MyBid);
                State++;
                
                break;
            case(1):
                // czekamy na wiadomość od brokera
                // inform podbito aukcję -> przejście do 0
                // Failure aukcja się zakończyła niepowodzeniem dla tego agenta -> 2
                // Accept_proposal agent wygrał -> 3
                ACLMessage msg = myAgent.blockingReceive(mt, 500);
                if(msg != null)
                {
                    
                    switch(msg.getPerformative())
                    {
                        case ACLMessage.ACCEPT_PROPOSAL:
                            // wygraliśmy
                            try 
                            {
                                myAgent.Cash = myAgent.Cash - (float)msg.getContentObject();
                                int tmpProductCount = myAgent.ProductsToBuy.get(myAgent.ActiveAuction.Product) - myAgent.ActiveAuction.Amount; //ilosć produktu potrzebna po kupnie
                                myAgent.ProductsToBuy.put(myAgent.ActiveAuction.Product, tmpProductCount);
                                float tmpProductRealValue = myAgent.ProductsRealValue.get(myAgent.ActiveAuction.Product);
                                myAgent.ProductsRealValue.put(myAgent.ActiveAuction.Product, tmpProductRealValue - tmpProductRealValue * (myAgent.Random.nextFloat()* myAgent.Aggressiveness));
                                
                            } catch (UnreadableException ex) 
                            {
                                Logger.getLogger(ManagerBehaviourBuyer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            State++;
                            break;
                        case ACLMessage.FAILURE:
                            // przegraliśmy dostosowujemy rzeczywistą wartosć na wyższą 
                            float tmpProductRealValue = myAgent.ProductsRealValue.get(myAgent.ActiveAuction.Product);
                            myAgent.ProductsRealValue.put(myAgent.ActiveAuction.Product, tmpProductRealValue + tmpProductRealValue * (myAgent.Random.nextFloat()* myAgent.Aggressiveness));
                            
                            State++;
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public boolean done() {
        if(State == 2)
        {
            myAgent.ActiveAuction = null;
            System.out.println("Buyer-agent "+myAgent.getAID().getName()+" Auction has ended.");
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
