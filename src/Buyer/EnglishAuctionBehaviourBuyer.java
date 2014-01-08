/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Buyer;

import Seller.AgentSeller;
import jade.Auction;
import jade.Bid;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.logging.Level;
import java.util.logging.Logger;

// zachowanie dla aukcji angielskiej kupującego
class EnglishAuctionBehaviourBuyer extends SimpleBehaviour {
    private AgentBuyer myAgent;
    private int State;
    private Bid MyBid; // bid tego Agenta
    private Bid HighestBid; // najwyższy aktualny bid
    private MessageTemplate mt;

    public EnglishAuctionBehaviourBuyer(AgentBuyer agent) {
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
                if( HighestBid == null) // początek aukcji nie ma jeszcze żadnych bidów
                {
                    float tmpBidValue = myAgent.ActiveAuction.StartingPrice + myAgent.ActiveAuction.MinimalStep;
                    tmpBidValue = tmpBidValue + tmpBidValue* (myAgent.Random.nextFloat()* myAgent.Aggressiveness); // tmpbid +  około 0-10 procent tmpbid
                    if(tmpBidValue > myAgent.Cash)
                    {
                        tmpBidValue = myAgent.Cash;
                    }
                    
                    MyBid = new Bid(tmpBidValue, myAgent.getAID());
                }
                else
                {
                    float tmpBidValue = HighestBid.Value + myAgent.ActiveAuction.MinimalStep;
                    tmpBidValue = tmpBidValue + tmpBidValue* (myAgent.Random.nextFloat()* myAgent.Aggressiveness);
                    if(tmpBidValue > myAgent.Cash)
                    {
                        tmpBidValue = myAgent.Cash;
                    }
                    MyBid = new Bid(tmpBidValue, myAgent.getAID());
                }
                
                // wprowadzam losowe opóźnienie, żeby aukcje trwały troche dluzej i mniej deterministycznie
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
                        case ACLMessage.INFORM:
                            try 
                            {
                                HighestBid = (Bid)msg.getContentObject();
                            } catch (UnreadableException ex) 
                            {
                                Logger.getLogger(ManagerBehaviourBuyer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            if(HighestBid.Value > myAgent.ProductsRealValue.get(myAgent.ActiveAuction.Product) * myAgent.ActiveAuction.Amount)
                            {
                                // wartość aktualnego bidu jest większa niż rzeczywista wartość dla agenta więc nie podbijamy dalej
                                return;
                            }
                            
                            if(HighestBid.Bidder != myAgent.getAID())
                            {
                                State--;
                                // w przeciwnym przypadku nie podbijamy bo aktualnie wygrywamy aukcję;
                            }
                            
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            // wygraliśmy
                            System.out.println("Buyer-agent "+myAgent.getAID().getName()+" has won auction  "+myAgent.ActiveAuction.Id+".");
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
