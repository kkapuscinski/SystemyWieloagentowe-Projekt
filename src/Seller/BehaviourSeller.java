package Seller;

import jade.AuctionParameters;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.Iterator;

// Klasa zachowania Agenta sprzedawcy
public class BehaviourSeller extends SimpleBehaviour 
{
	private AgentSeller myAgent; // Agent na rzecz którego wykonywane jest zachowanie
        int State = 0; // stan maszyny stanowej
	public BehaviourSeller(AgentSeller agentSeller) 
        {
            myAgent = agentSeller;
	}

	@Override
	public void action() 
        {
            switch(State)
            {
                case(0):
                    // oczekujemy na wiadomość CFP od brokera aby wysłać mu nasze propozycje aukcji
                    MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("auction-proposal"), MessageTemplate.MatchPerformative(ACLMessage.CFP));
                    ACLMessage cfp = myAgent.blockingReceive(mt, 500);
                    if(cfp != null)
                    {
                        myAgent.BrokerAID = cfp.getSender();
                        for (int i = 0; i < myAgent.Auctions.size(); i++) 
                        {
                            ACLMessage msg = cfp.createReply();
                            msg.setPerformative(ACLMessage.PROPOSE);
                            try 
                            {
                                msg.setContentObject(myAgent.Auctions.get(i).getAuction());
                                myAgent.send(msg);
                                myAgent.Auctions.get(i).AuctionState = SellerAuctionState.SentToBroker;
                            } catch (Exception ex) 
                            {
                                ex.printStackTrace();
                            }
                        }
                        State++;
                    }
                    break;
                case(1):
                    // Po wysłaniu Aukcji oczekujemy potwierdzenia przyjęcia aukcji
                    MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.MatchConversationId("auction-proposal"), MessageTemplate.MatchSender(myAgent.BrokerAID));
                    ACLMessage receivedMessage = myAgent.blockingReceive(mt1, 500);
                    if(receivedMessage != null)
                    {
                        int msgPerformative = receivedMessage.getPerformative();
                        if(msgPerformative == ACLMessage.ACCEPT_PROPOSAL)
                        {
                            // zapisz informacje o powodzeniu i czekaj na zakończenie aukcji
                            try 
                            {
                                AuctionParameters receivedAuction = (AuctionParameters)receivedMessage.getContentObject();
                                boolean everyAuctionAccepted = true;
                                for (Iterator<SellerAuction> it = myAgent.Auctions.iterator(); it.hasNext();) {
                                    SellerAuction tmpSAuction = it.next();
                                    if (tmpSAuction.getAuction().SellerAID == myAgent.getAID()) 
                                    {
                                        if(tmpSAuction.getAuction().Id == receivedAuction.Id)
                                        {
                                            tmpSAuction.AuctionState = SellerAuctionState.AcceptedByBroker;
                                        }
                                        if(tmpSAuction.AuctionState != SellerAuctionState.AcceptedByBroker)
                                        {
                                            everyAuctionAccepted = false;
                                        }
                                    }

                                }
                                if(everyAuctionAccepted)
                                {
                                    State++;
                                }
                            } 
                            catch (UnreadableException ex) 
                            {
                                ex.printStackTrace();
                            }
                        }
                    }
                    
                    break;
                case(2):
                    // czekamy na informacje o zakończeniu aukcji
                    MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchConversationId("auction-proposal"), MessageTemplate.MatchSender(myAgent.BrokerAID));
                    ACLMessage receivedMessage2 = myAgent.blockingReceive(mt2, 500);
                    if(receivedMessage2 != null)
                    {
                        int msgPerformative = receivedMessage2.getPerformative();
                        if(msgPerformative == ACLMessage.FAILURE)
                        {
                         //aukcja nie została sprzedana oznaczamy ten fakt
                         try 
                            {
                                AuctionParameters receivedAuction = (AuctionParameters)receivedMessage2.getContentObject();
                                for (Iterator<SellerAuction> it = myAgent.Auctions.iterator(); it.hasNext();) {
                                    SellerAuction tmpSAuction = it.next();
                                    if (tmpSAuction.getAuction().SellerAID == myAgent.getAID()) 
                                    {
                                        if(tmpSAuction.getAuction().Id == receivedAuction.Id)
                                        {
                                            tmpSAuction.AuctionState = SellerAuctionState.NotSold;
                                        }
                                    }

                                }
                            } 
                            catch (UnreadableException ex) 
                            {
                                ex.printStackTrace();
                            }
                        }
                        else if(msgPerformative == ACLMessage.INFORM)
                        {
                            //aukcja została sprzedana oznaczamy ten fakt
                            try 
                            {
                                AuctionParameters receivedAuction = (AuctionParameters)receivedMessage2.getContentObject();
                                for (Iterator<SellerAuction> it = myAgent.Auctions.iterator(); it.hasNext();) {
                                    SellerAuction tmpSAuction = it.next();
                                    if (tmpSAuction.getAuction().SellerAID == myAgent.getAID()) 
                                    {
                                        if(tmpSAuction.getAuction().Id == receivedAuction.Id)
                                        {
                                            tmpSAuction.AuctionState = SellerAuctionState.Sold;
                                        }
                                    }

                                }
                            } 
                            catch (UnreadableException ex) 
                            {
                                ex.printStackTrace();
                            }
                        }
                        // sprawdzamy czy wszystkie aukcje się zakończyły
                        boolean auctionEnd = true;
                        for (Iterator<SellerAuction> it = myAgent.Auctions.iterator(); it.hasNext();) 
                        {
                            SellerAuction tmpSAuction = it.next();
                            if(!(tmpSAuction.AuctionState == SellerAuctionState.Sold || tmpSAuction.AuctionState == SellerAuctionState.NotSold))
                            {
                                auctionEnd = false;
                            }

                        }
                        // jeśli tak to kończymy behaviour
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
            // stan 3 oznacza że aukcje się zakończyły i można zakończyć behaviour
            if(State == 3)
            {
                System.out.println("Seller-agent "+myAgent.getAID().getName()+" auctions has ended.");
                return true;
            }
            else
            {
                return false;
            }
	}
        
        

}
