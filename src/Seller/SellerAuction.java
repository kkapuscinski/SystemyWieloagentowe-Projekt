/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Seller;

import jade.AuctionParameters;

/**
 *
 * @author Karol
 */
public class SellerAuction 
{
    private AuctionParameters Auction;
    public  SellerAuctionState AuctionState;

    /**
     * @return the Auction
     */
    public AuctionParameters getAuction() 
    {
        return Auction;
    }
    
    public SellerAuction(AuctionParameters auction)
    {
        Auction = auction;
        AuctionState = AuctionState.Created;
    }
    
}
