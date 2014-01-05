/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.Auction;

/**
 *
 * @author Karol
 */
public class BrokerAuction 
{
    
    private Auction Auction;
    public BrokerAuctionState AuctionState;
    private int BrokerAuctionId;

    
    public BrokerAuction(Auction auction, int id)
    {
        Auction = auction;
        AuctionState = AuctionState.Created;
        BrokerAuctionId = id;
    }
    
    /**
     * @return the Auction
     */
    public Auction getAuction() 
    {
        return Auction;
    }

    /**
     * @return the BrokerAuctionId
     */
    public int getBrokerAuctionId() {
        return BrokerAuctionId;
    }
}
