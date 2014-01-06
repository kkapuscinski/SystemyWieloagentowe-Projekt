/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Karol
 */
class VickereyAuctionBehaviourBroker extends TickerBehaviour {

    private AgentBroker myAgent;
    private int State;
    private BrokerAuction ActiveAuction;
    private MessageTemplate mt;
    private int NoPropositionOffers;
    
    VickereyAuctionBehaviourBroker(AgentBroker agent, BrokerAuction activeAuction, long period)
    {
        super(agent, period);
        myAgent = agent;
        State = 0;
        NoPropositionOffers = 0;
        ActiveAuction = activeAuction;
    }

    @Override
    protected void onTick() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
