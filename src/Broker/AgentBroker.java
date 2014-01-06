/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import Seller.AgentSeller;
import jade.AgentExtension;
import jade.AuctionParameters;
import jade.Bid;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karol
 */
public class AgentBroker extends Agent {
    public List<BrokerAuction> Auctions;
    public Codec Codec = new SLCodec();
    public List<AID> Sellers;
    public List<AID> Buyers;
    
    protected void setup() 
    {
        Auctions = new ArrayList<>();

        System.out.println("Hello! Broker-agent "+getAID().getName()+" is ready.");
        Object[] args = getArguments();
        getContentManager().registerLanguage(Codec);

        AgentExtension.DFRegister(this, "Broker");
        try {
            Thread.sleep(13000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AgentSeller.class.getName()).log(Level.SEVERE, null, ex);
        }
        Sellers = AgentExtension.findAgentsByType(this, "Seller");
        Buyers = AgentExtension.findAgentsByType(this, "Buyer");
        AcceptOffersBehaviourBroker behaviourBroker = new AcceptOffersBehaviourBroker(this);
        addBehaviour(behaviourBroker);
    }

    protected void takeDown() 
    {
        System.out.println("Broker-agent "+getAID().getName()+" terminating.");
    }
    
    public MessageTemplate sendAuctionCFPToBuyers(BrokerAuction auction)
    {
       // wysyłamy informację o aukcji do kupców o rozpoczęciu aukcji 
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        for (int i = 0; i < Buyers.size(); ++i) {
            msg.addReceiver(Buyers.get(i));
        }
        msg.setConversationId("Auction-"+auction.Auction.Id);
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
    
    
    public void sendAuctionHighestBidToBuyers(BrokerAuction auction)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        for (int i = 0; i < Buyers.size(); ++i) {
            msg.addReceiver(Buyers.get(i));
        }
        msg.setConversationId("Auction-"+auction.Auction.Id);
        try {
            msg.setContentObject(auction.HighestBid);
            send(msg);
        } catch (IOException ex) {
            Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendAuctionEnd(BrokerAuction auction) 
    {
        ACLMessage msgForLossers = new ACLMessage(ACLMessage.FAILURE);
        for (int i = 0; i < Buyers.size(); ++i) {
            if(!(Buyers.get(i) == auction.HighestBidder) || auction.AuctionState == BrokerAuctionState.NotSold)
            {
                msgForLossers.addReceiver(Buyers.get(i));
            }
        }
        msgForLossers.setConversationId("Auction-"+auction.Auction.Id);
        send(msgForLossers);
        
        if(auction.AuctionState == BrokerAuctionState.Sold)
        {
            ACLMessage msgForWinner = new ACLMessage(ACLMessage.INFORM);
            msgForWinner.addReceiver(auction.HighestBidder);
            msgForWinner.setConversationId("Auction-"+auction.Auction.Id);
            try {
                msgForWinner.setContentObject(auction.HighestBid);
                send(msgForWinner);
            } catch (IOException ex) {
                Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
            }
            auction.AuctionParameters.SoldPrice = auction.HighestBid.Value;
            ACLMessage msgForSeller = new ACLMessage(ACLMessage.INFORM);
            msgForSeller.addReceiver(auction.AuctionParameters.SellerAID);
            msgForSeller.setConversationId("Auction-"+auction.AuctionParameters.Id);
            try {
                msgForSeller.setContentObject(auction.AuctionParameters);
                send(msgForSeller);
            } catch (IOException ex) {
                Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            ACLMessage msgForSeller = new ACLMessage(ACLMessage.FAILURE);
            msgForSeller.addReceiver(auction.AuctionParameters.SellerAID);
            msgForSeller.setConversationId("Auction-"+auction.AuctionParameters.Id);
            
            try {
                msgForSeller.setContentObject(auction.AuctionParameters);
                send(msgForSeller);
            } catch (IOException ex) {
                Logger.getLogger(AuctionManagerBehaviourBroker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
