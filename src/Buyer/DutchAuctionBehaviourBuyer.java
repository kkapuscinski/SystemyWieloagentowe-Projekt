/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Buyer;

import jade.Bid;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.logging.Level;
import java.util.logging.Logger;

//zachowanie kupującego dla aukcji holenderskiej
class DutchAuctionBehaviourBuyer extends SimpleBehaviour {
    private AgentBuyer myAgent;
    private int State;
    private Bid MyBid;
    private Bid ActualBid;
    private MessageTemplate mt;
    
    public DutchAuctionBehaviourBuyer(AgentBuyer agent) {
        myAgent = agent;
        State = 0;
        mt =MessageTemplate.and(MessageTemplate.MatchConversationId("Auction-Buyers-Proposals"), MessageTemplate.MatchSender(myAgent.ActiveauctionCfpMsg.getSender()));
        
    }

    @Override
    public void action() {
        switch(State)
        {
            case(0):
                // ustalamy cenę aukcji dla agenta
                float tmpBidValue = myAgent.ProductsRealValue.get(myAgent.ActiveAuction.Product) * myAgent.ActiveAuction.Amount;
                tmpBidValue = tmpBidValue + tmpBidValue* (myAgent.Random.nextFloat()* myAgent.Aggressiveness);
                if(tmpBidValue > myAgent.Cash)
                {
                    tmpBidValue = myAgent.Cash;
                }
                MyBid = new Bid(tmpBidValue, myAgent.getAID());
                State++;
                break;
            case(1):
                ACLMessage msg = myAgent.blockingReceive(mt, 500);
                if(msg != null)
                {
                    
                    switch(msg.getPerformative())
                    {
                        case ACLMessage.INFORM:
                            try 
                            {
                                ActualBid = (Bid)msg.getContentObject();
                            } catch (UnreadableException ex) 
                            {
                                Logger.getLogger(ManagerBehaviourBuyer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            
                            
                            if(ActualBid.Value > MyBid.Value)
                            {
                                // wartość aktualnego bidu jest większa niż rzeczywista wartość dla agenta więc czekamy dalej
                                return;
                            }
                            else
                            {
                                ActualBid.Bidder = myAgent.getAID();
                                myAgent.SendAuctionBid(ActualBid);
                            }
                            
                            break;
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
