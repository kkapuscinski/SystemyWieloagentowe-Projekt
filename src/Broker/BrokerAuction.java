/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.Auction;
import jade.AuctionParameters;
import jade.AuctionType;
import jade.Bid;
import jade.core.AID;
import java.util.Random;

/**
 *
 * @author Karol
 */
public class BrokerAuction 
{
    
    public AuctionParameters AuctionParameters;
    public BrokerAuctionState AuctionState;
    public int BrokerAuctionId;
    public Auction Auction;
    public Bid HighestBid;
    public AID HighestBidder;
    public Bid SecondHighestBid; // tylko do vickereya

    
    public BrokerAuction(AuctionParameters auctionParameters, int id)
    {
        AuctionParameters = auctionParameters;
        AuctionState = AuctionState.Created;
        BrokerAuctionId = id;
        Random random = new Random();
        AuctionType[] values = AuctionType.values();
        AuctionType aucttype = values[random.nextInt(3)];
        Auction = new Auction(id, AuctionParameters.Product, aucttype, AuctionParameters.Amount, AuctionParameters.MinimalStep);
        
    }

}
