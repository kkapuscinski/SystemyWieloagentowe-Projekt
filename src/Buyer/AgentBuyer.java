package Buyer;

import Seller.AgentSeller;
import jade.AgentExtension;
import jade.Auction;
import jade.Bid;
import jade.ProductType;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

// klasa opisująca kupującego
public class AgentBuyer extends Agent 
{
    public EnumMap<ProductType, Integer> ProductsToBuy; // lista produktów do kupna wraz z ilością zapotrzebowania
    public EnumMap<ProductType, Float> ProductsRealValue; // lista rzeczywistych wartości produktów dla agenta
    public float Cash; // ilość pieniędzy posiadanych przez agenta
    public float Aggressiveness; // Agresywność agenta
    public Random Random; // generator liczb losowych
    public Auction ActiveAuction; // aktualnie prowadzona aukcja
    public ACLMessage ActiveauctionCfpMsg; // Wiadomość CFP od brokera dla aktualnej aukcji
    public List<Auction> WonAuctions; // lista wygranych aukcji

    protected void setup() 
    {
        System.out.println("Hello! Buyer-agent "+getAID().getName()+" is ready.");
        WonAuctions = new ArrayList<>();
        // losujemy produkty do kupna oraz ich zapotrzebowanie
        ProductsToBuy = new EnumMap<>(ProductType.class);
        ProductsRealValue = new EnumMap<>(ProductType.class);
        Random = new Random();
        ProductType prodtype =null;
        for (int i = 0; i < 2; i++) {
            ProductType[] values = ProductType.values();
            ProductType tmp2prodtype = values[Random.nextInt(5)];
            while (tmp2prodtype == prodtype) {                
                tmp2prodtype = values[Random.nextInt(5)];
            }
            prodtype = tmp2prodtype;
            
            int amountOfProduct = Random.nextInt(20)+1;
            ProductsToBuy.put(prodtype, amountOfProduct);
            float tmpValue = 30 + 30*((Random.nextFloat()/10)-0.05F); // wartosć realna dla agenta danego produktu 30 +- 10%
            ProductsRealValue.put(prodtype, tmpValue);
        }
        
        
        // argumenty (ilość pieniędzy i agresywność
        Object[] args = getArguments();
        if (args != null && args.length > 0) 
        {
            Cash = Float.parseFloat((String)args[0]);
            Aggressiveness = Float.parseFloat((String)args[1]);
        }
        else 
        {
            System.out.println("inssuficient parameters");
            doDelete();
        }
        
        AgentExtension.DFRegister(this, "Buyer");
        // odczekujemy 10 sekund aż broker przyjmie oferty sprzedaży od sprzedających
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AgentSeller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        addBehaviour(new ManagerBehaviourBuyer(this));
    }

    protected void takeDown() 
    {
        System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
    }

    //metoda wysyłająca bid do brokera
    public void SendAuctionBid(Bid myBid)
    {
        ACLMessage msg = ActiveauctionCfpMsg.createReply();
        msg.setPerformative(ACLMessage.PROPOSE);
        try 
        {
            msg.setContentObject(myBid);
            send(msg);
        } catch (Exception ex) 
        {
            Logger.getLogger(AgentSeller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
