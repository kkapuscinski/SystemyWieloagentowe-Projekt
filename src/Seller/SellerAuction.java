/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Seller;

import jade.Auction;

/**
 *
 * @author Karol
 */
public class SellerAuction 
{
    private Auction Auction;
    public  AuctionState AuctionState;

    /**
     * @return the Auction
     */
    public Auction getAuction() 
    {
        return Auction;
    }
    
    public SellerAuction(Auction auction)
    {
        Auction = auction;
        AuctionState = AuctionState.Created;
    }
    
}
