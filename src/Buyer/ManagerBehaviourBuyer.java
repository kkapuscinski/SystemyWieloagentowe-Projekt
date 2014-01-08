package Buyer;

import jade.Auction;
import jade.ProductType;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

// zachowanie zarządzające wyborem typu aukcji
public class ManagerBehaviourBuyer extends SimpleBehaviour {

    private AgentBuyer myAgent;
    private MessageTemplate mt;
    private int State;

    public ManagerBehaviourBuyer (AgentBuyer agent)
    {
      myAgent = agent;
      State = 0;
      mt =MessageTemplate.and(MessageTemplate.MatchConversationId("Auction-Buyers-Proposals"),MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchPerformative(ACLMessage.CANCEL)) );
    }

    public void action() 
    {	
        switch(State)
        {
            case(0):
                // oczekiwanie na cfp
                myAgent.ActiveauctionCfpMsg = myAgent.blockingReceive(mt, 1000);
                if(myAgent.ActiveauctionCfpMsg != null)
                {
                    if(myAgent.ActiveauctionCfpMsg.getPerformative() == ACLMessage.CANCEL)
                    {
                        // wszystkie aukcje zakończone
                        State = 2;
                        return;
                    }
                    
                    try 
                    {
                        myAgent.ActiveAuction = (Auction)myAgent.ActiveauctionCfpMsg.getContentObject();
                    } catch (UnreadableException ex) 
                    {
                        Logger.getLogger(ManagerBehaviourBuyer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if((myAgent.ProductsToBuy.containsKey(myAgent.ActiveAuction.Product) // jest to produkt na liście potrzeb
                            && myAgent.ProductsToBuy.get(myAgent.ActiveAuction.Product) > 0) // i ilość do kupienia jest większa niż 0
                            && myAgent.ActiveAuction.StartingPrice + myAgent.ActiveAuction.MinimalStep < myAgent.Cash) // cena wywoławcza + minimalny krok jest mniejsza niz ilosć posiadanych pieniędzy
                    {
                        
                        // startujemy zachowanie odpowiedniej aukcji
                        switch(myAgent.ActiveAuction.AuctionType)
                        {
                            case English:
                                myAgent.addBehaviour(new EnglishAuctionBehaviourBuyer(myAgent));
                                break;

                            case Dutch:
                                myAgent.addBehaviour(new DutchAuctionBehaviourBuyer(myAgent));
                                break;

                            case Vikerey:
                                myAgent.addBehaviour(new VikereyAuctionBehaviourBuyer(myAgent));
                                break;
                        }
                    }
                    else
                    {
                        // agent nie bierze udziału w aukcji jeśli nie spełnia warunków
                        myAgent.ActiveAuction = null;
                        System.out.println("Buyer-agent "+myAgent.getAID().getName()+" Is not intrested in auction.");
                    }
                    State++;
                }
                break;
            case(1):
                // czekanie na zakończenie aktualnej aukcji
                if(myAgent.ActiveAuction == null)
                {
                    State--;
                }
                
                break;
        }

    }




    @Override
    public boolean done() {
        if(State == 2)
        {
            System.out.println("Buyer-agent "+myAgent.getAID().getName()+" All auctions has ended. cash ="+myAgent.Cash);

            for(Entry<ProductType , Integer> entry : myAgent.ProductsToBuy.entrySet()){      
                System.out.println("Buyer-agent "+myAgent.getAID().getName()+" Product " + entry.getKey() + ", count " + entry.getValue());     
                
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }

}
