/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jade;

import jade.core.AID;
import java.io.Serializable;

/**
 *
 * @author Karol
 */
public class Auction implements Serializable
{
    public int Id;
    public ProductType Product;
    public int Amount;
    public float EnglishAuctionStartingPrice;
    public float DutchAuctionStartingPrice;
    public float AuctionMinimalPrice;
    public float MinimalStep;
    public AID SellerAID;
    
    public Auction(int id, ProductType product, int amount, float easp, float dasp, float amp,  float minimalStep, AID sellerAID)
    {
        Id = id;
        Product = product;
        Amount = amount;
        EnglishAuctionStartingPrice = easp;
        DutchAuctionStartingPrice = dasp;
        AuctionMinimalPrice = amp;
        MinimalStep = minimalStep;
        SellerAID = sellerAID;
    }
}
