/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jade;

import java.io.Serializable;

/**
 *
 * @author Karol
 */
public class Auction implements Serializable {
    public int Id;
    public ProductType Product;
    public AuctionType AuctionType;
    public int Amount;
    public float MinimalStep;
    
    public Auction(int id, ProductType product, AuctionType auctionType, int amount, float minimalStep)
    {
        Id = id;
        Product = product;
        AuctionType = auctionType;
        Amount = amount;
        MinimalStep = minimalStep;
        
    }
    
}
