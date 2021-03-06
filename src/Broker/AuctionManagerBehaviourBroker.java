/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.core.behaviours.SimpleBehaviour;

// zachowanie brokera zarządzające prowadzeniem wszystkich aukcji
class AuctionManagerBehaviourBroker extends SimpleBehaviour 
{
    private AgentBroker myAgent;
    private int State; // stan maszyny stanowej
    
    
    
    public AuctionManagerBehaviourBroker(AgentBroker agent) 
    {
        myAgent = agent;
        State = 0;
    }

    @Override
    public void action() 
    {
        switch(State)
        {
            case(0):
                // wybieramy pierwszą nie zakończoną aukcję i uruchamiamy odpowiedni beahviur dla typu aukcji
                boolean AllAcuctionsHasEnded = true;
                for (int i = 0; i < myAgent.Auctions.size(); i++) {
                    if(!(myAgent.Auctions.get(i).AuctionState == BrokerAuctionState.Sold || myAgent.Auctions.get(i).AuctionState == BrokerAuctionState.NotSold))
                    {
                        myAgent.ActiveAuction = myAgent.Auctions.get(i);
                        AllAcuctionsHasEnded = false;
                        break;
                    }
                }
                if (AllAcuctionsHasEnded)
                {
                    State = 2;
                    return;
                }
                
                switch(myAgent.ActiveAuction.Auction.AuctionType)
                {
                    case Dutch:
                        myAgent.addBehaviour(new DutchAuctionBehaviourBroker(myAgent, 1000));
                        break;
                        
                    case English:
                        myAgent.addBehaviour(new EnglishAuctionBehaviourBroker(myAgent, 1000));
                        break;
                    
                    case Vikerey:
                        myAgent.addBehaviour(new VickereyAuctionBehaviourBroker(myAgent, 200));
                        break;
                        
                }
                State++;
                
                break;
            case(1):
                // jeśli aktywna aukcja się zakończyła ponownie wybieramy kolejną aukcję do prowadzenia
                if((myAgent.ActiveAuction.AuctionState == BrokerAuctionState.Sold || myAgent.ActiveAuction.AuctionState == BrokerAuctionState.NotSold))
                {
                    System.out.println("Broker-agent auction"+myAgent.ActiveAuction.BrokerAuctionId+" has ended.");
                    State--;
                }
                break;
        }
        
    }

    @Override
    public boolean done() {
        if(State == 2)
        {
            // wyślij informację do kupujących że wszystkie aukcje się zakończyły
            myAgent.sendAuctionCancelToBuyers();
            
            // print przeprowadzonych aukcji i ich parametrów
            System.out.println("Broker-agent "+myAgent.getAID().getName()+" All auctions had ended.");
            
            System.out.println("Broker-agent "+myAgent.getAID().getName()+" Status:");
            for (int i = 0; i < myAgent.Auctions.size(); i++) {
                System.out.println("Broker-agent "+myAgent.getAID().getName()+"auction params:"+myAgent.Auctions.get(i).AuctionParameters.Amount+", "+myAgent.Auctions.get(i).AuctionParameters.Product.toString()+" auction state:"+myAgent.Auctions.get(i).AuctionState.toString()+" sold for: "+myAgent.Auctions.get(i).AuctionParameters.SoldPrice);
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
