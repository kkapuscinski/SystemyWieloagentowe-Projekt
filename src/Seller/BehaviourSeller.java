package Seller;

import jade.Auction;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BehaviourSeller extends SimpleBehaviour 
{
	private AgentSeller Agent;
        int State = 0;
	public BehaviourSeller(AgentSeller agentSeller) 
        {
            Agent = agentSeller;
	}

	@Override
	public void action() 
        {
            switch(State)
            {
                case(0):
                    for (int i = 0; i < Agent.Auctions.size(); i++) 
                    {
                        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                        msg.setLanguage(Agent.Codec.getName());
                        msg.addReceiver(Agent.BrokerAID);
                        try 
                        {
                            msg.setContentObject(Agent.Auctions.get(i).getAuction());
                            Agent.send(msg);
                            Agent.Auctions.get(i).AuctionState = AuctionState.SentToBroker;
                        } catch (Exception ex) 
                        {
                            ex.printStackTrace();
                        }
                    }
                    State++;
                    break;
                case(1):
                    
                    ACLMessage receivedMessage = Agent.blockingReceive(500);
                    if(receivedMessage != null)
                    {
                      
                        if(receivedMessage.getSender() != Agent.BrokerAID)
                        {
                            //nieznany nadawca
                            ACLMessage reply = receivedMessage.createReply();
                            reply.setPerformative(ACLMessage.UNKNOWN);
                            try 
                            {
                                Agent.send(reply);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                        int msgPerformative = receivedMessage.getPerformative();
                        if(msgPerformative == ACLMessage.ACCEPT_PROPOSAL)
                        {
                            // zapisz informacje o powodzeniu i czekaj na zakończenie aukcji
                            try 
                            {
                                Auction receivedAuction = (Auction)receivedMessage.getContentObject();
                                for (Iterator<SellerAuction> it = Agent.Auctions.iterator(); it.hasNext();) {
                                    SellerAuction tmpSAuction = it.next();
                                    if (tmpSAuction.getAuction().SellerAID == Agent.getAID()) 
                                    {
                                        if(tmpSAuction.getAuction().Id == receivedAuction.Id)
                                        {
                                            tmpSAuction.AuctionState = AuctionState.AcceptedByBroker;
                                        }
                                    }
                                    else
                                    {
                                        // aukcja nie tego sprzedawcy
                                    }

                                }
                            } 
                            catch (UnreadableException ex) 
                            {
                                ex.printStackTrace();
                            }
                        }
                        else if(msgPerformative == ACLMessage.REJECT_PROPOSAL)
                        {
                            // nie znam przyczyny dlaczego miało by się tak stać
                        }
                        else if(msgPerformative == ACLMessage.INFORM)
                        {
                            //aukcja została zakończona pozytywnie
                            try 
                            {
                                Auction receivedAuction = (Auction)receivedMessage.getContentObject();
                                for (Iterator<SellerAuction> it = Agent.Auctions.iterator(); it.hasNext();) {
                                    SellerAuction tmpSAuction = it.next();
                                    if (tmpSAuction.getAuction().SellerAID == Agent.getAID()) 
                                    {
                                        if(tmpSAuction.getAuction().Id == receivedAuction.Id)
                                        {
                                            tmpSAuction.AuctionState = AuctionState.Sold;
                                        }
                                    }
                                    else
                                    {
                                        // aukcja nie tego sprzedawcy
                                    }

                                }
                            } 
                            catch (UnreadableException ex) 
                            {
                                ex.printStackTrace();
                            }
                        }
                        else if(msgPerformative == ACLMessage.FAILURE)
                        {
                            //aukcja zakończyła się nie powodzeniem TODO
                        }
                        else
                        {
                            // nieznany rodzaj komunikatu
                            ACLMessage reply = receivedMessage.createReply();
                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                            try 
                            {
                                Agent.send(reply);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                        boolean auctionEnd = true;
                        for (Iterator<SellerAuction> it = Agent.Auctions.iterator(); it.hasNext();) 
                        {
                            SellerAuction tmpSAuction = it.next();
                            if(!(tmpSAuction.AuctionState == AuctionState.Sold || tmpSAuction.AuctionState == AuctionState.NotSold))
                            {
                                auctionEnd = false;
                            }

                        }
                        if(auctionEnd)
                        {
                            State++;
                        }
                      
                    }
                    break;
                    
            } 
	}

	@Override
	public boolean done() 
        {
            if(State == 2)
            {
                System.out.println("Seller-agent "+Agent.getAID().getName()+" auctions has ended.");
                return true;
            }
            else
            {
                return false;
            }
	}
        
        

}
