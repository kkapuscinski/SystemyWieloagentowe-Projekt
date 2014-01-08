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

// kontener aukcji brokera
public class BrokerAuction 
{
    
    public AuctionParameters AuctionParameters; // parametry aukcji od sprzedawcy
    public BrokerAuctionState AuctionState; // stan aukcji u brokera
    public int BrokerAuctionId; // Id aukcji brokera
    public Auction Auction; // obiekt aukcji przeznaczony dla kupujących
    public Bid HighestBid; // aukcja angielska/vickereya
    public Bid SecondHighestBid; // aukcja vickereya
    public Bid ActualBid; // aukcja holenderska

    
    public BrokerAuction(AuctionParameters auctionParameters, int id)
    {
        AuctionParameters = auctionParameters;
        AuctionState = AuctionState.Created;
        BrokerAuctionId = id;
        // losujemy typ aukcji
        Random random = new Random();
        AuctionType[] values = AuctionType.values();
        //AuctionType aucttype = AuctionType.Dutch;
        AuctionType aucttype = values[random.nextInt(3)];
        
        float startingPrice = 0;
        // ustawiamy cenę startową aukcji kupującego w zależności od typu aukcji
        switch(aucttype)
        {
            case English:
                startingPrice = auctionParameters.EnglishAuctionStartingPrice;
                break;
            case Dutch:
                startingPrice = auctionParameters.DutchAuctionStartingPrice;
                break;
            case Vikerey:
                startingPrice = 0;
        }
        Auction = new Auction(id, AuctionParameters.Product, aucttype, AuctionParameters.Amount, AuctionParameters.MinimalStep, startingPrice);
        
    }

}
