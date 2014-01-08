/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import Seller.AgentSeller;
import jade.AgentExtension;
import jade.AuctionType;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Klasa opisująca Agenta brokera aukcji
public class AgentBroker extends Agent {
    public List<BrokerAuction> Auctions; // lista aukcji prowadzonych przez brokera
    public Codec Codec = new SLCodec();
    public List<AID> Sellers; // Lista Agentów sprzedawców
    public List<AID> Buyers; // Lista Agentów kupców
    public BrokerAuction ActiveAuction; // Aktywnie prowadzona aukcja przez brokera
    
    protected void setup() 
    {
        Auctions = new ArrayList<>();

        System.out.println("Hello! Broker-agent "+getAID().getName()+" is ready.");
        getContentManager().registerLanguage(Codec);

        AgentExtension.DFRegister(this, "Broker");
        // opóźniamy lekko brokera
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AgentSeller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // pobieramy z DF agentów sprzedających i kupujących
        Sellers = AgentExtension.findAgentsByType(this, "Seller");
        Buyers = AgentExtension.findAgentsByType(this, "Buyer");
        
        // uruchamiamy zachowanie zajmujące sięprzyjmowaniem ofert od sprzedawców
        AcceptOffersBehaviourBroker behaviourBroker = new AcceptOffersBehaviourBroker(this);
        addBehaviour(behaviourBroker);
    }

    protected void takeDown() 
    {
        System.out.println("Broker-agent "+getAID().getName()+" terminating.");
    }
    
    // Metoda wysyłająca CFP do kupujących
    public MessageTemplate sendAuctionCFPToBuyers(BrokerAuction auction)
    {
       // wysyłamy informację o aukcji do kupców o rozpoczęciu aukcji 
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        for (int i = 0; i < Buyers.size(); ++i) {
            msg.addReceiver(Buyers.get(i));
        }
        msg.setConversationId("Auction-Buyers-Proposals");
        msg.setReplyWith("Auction-"+System.currentTimeMillis()); // Unique value
        try {
            msg.setContentObject(auction.Auction);
            send(msg);
        } catch (IOException ex) {
            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Prepare the template to get proposals
        return MessageTemplate.and(MessageTemplate.MatchConversationId(msg.getConversationId()),MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
    }
    
    // Metoda wysyłająca informację do kupujących o zakończeniu wszystkich aukcji
    public void sendAuctionCancelToBuyers()
    {
        ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
        for (int i = 0; i < Buyers.size(); ++i) {
            msg.addReceiver(Buyers.get(i));
        }
        msg.setConversationId("Auction-Buyers-Proposals");
        send(msg);
    }
    
    
    // metoda wysyłającą aktualny bid dla aukcji
    public void sendAuctionActualBidToBuyers(BrokerAuction auction)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        for (int i = 0; i < Buyers.size(); ++i) {
            msg.addReceiver(Buyers.get(i));
        }
        msg.setConversationId("Auction-Buyers-Proposals");
        try {
            if(auction.Auction.AuctionType == AuctionType.English)
            {
                msg.setContentObject(auction.HighestBid);
            }
            else
            {
                msg.setContentObject(auction.ActualBid);
            }
            send(msg);
        } catch (IOException ex) {
            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // metoda wysyłająca wiadomości do kupujących i sprzedającego o braku zwycięzcy
    void sendAuctionNotSoldEnd(BrokerAuction auction)
    {
        
        // widomość do kupców o tym że nie wygrali aukcji
        ACLMessage msgForLossers = new ACLMessage(ACLMessage.FAILURE);
        for (int i = 0; i < Buyers.size(); ++i) {
                msgForLossers.addReceiver(Buyers.get(i));
        }
        msgForLossers.setConversationId("Auction-Buyers-Proposals");
        send(msgForLossers);
        
        // wiadomość dla sprzedawcy, że towar się nie sprzedał
        ACLMessage msgForSeller = new ACLMessage(ACLMessage.FAILURE);
        msgForSeller.addReceiver(auction.AuctionParameters.SellerAID);
        msgForSeller.setConversationId("auction-proposal");

        try {
            msgForSeller.setContentObject(auction.AuctionParameters);
            send(msgForSeller);
        } catch (IOException ex) {
            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // metoda wysyłająca wiadomość do kupców i sprzedającego o tym że aukcja zakończyłą się pomyślnie
    void sendAuctionSoldEnd(BrokerAuction auction) 
    {
        AID winner = null;
        
        switch(auction.Auction.AuctionType)
        {
            case English:
                auction.AuctionParameters.SoldPrice = auction.HighestBid.Value;
                winner = auction.HighestBid.Bidder;
                break;

            case Dutch:
                auction.AuctionParameters.SoldPrice = auction.ActualBid.Value;
                winner = auction.ActualBid.Bidder;
                break;

            case Vikerey:
                auction.AuctionParameters.SoldPrice = auction.SecondHighestBid.Value;
                winner = auction.HighestBid.Bidder;
                break;
        }
        
        // widomość do kupców o tym że nie wygrali aukcji
        ACLMessage msgForLossers = new ACLMessage(ACLMessage.FAILURE);
        for (int i = 0; i < Buyers.size(); ++i) {
            if(!(Buyers.get(i) == winner))
            {
                msgForLossers.addReceiver(Buyers.get(i));
            }
        }
        msgForLossers.setConversationId("Auction-Buyers-Proposals");
        send(msgForLossers);
        
        // widomość do wygranego kupcy z stawką jaką zapłaci
        ACLMessage msgForWinner = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msgForWinner.addReceiver(winner);
        msgForWinner.setConversationId("Auction-Buyers-Proposals");
        try {
            msgForWinner.setContentObject(auction.AuctionParameters.SoldPrice);
            send(msgForWinner);
        } catch (IOException ex) {
            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        // wiadomość do sprzedawcy o tym za ile sprzedano towar
        ACLMessage msgForSeller = new ACLMessage(ACLMessage.INFORM);
        msgForSeller.addReceiver(auction.AuctionParameters.SellerAID);
        msgForSeller.setConversationId("auction-proposal");
        try {
            msgForSeller.setContentObject(auction.AuctionParameters);
            send(msgForSeller);
        } catch (IOException ex) {
            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
