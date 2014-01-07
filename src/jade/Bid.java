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
public class Bid implements Serializable {
    public float Value;
    public AID Bidder;
    
    public Bid(float value, AID buyer)
    {
        Value = value;
        Bidder = buyer;
    }
}
