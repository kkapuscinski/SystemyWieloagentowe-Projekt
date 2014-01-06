/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Karol
 */
class DutchAuctionBehaviourBroker extends TickerBehaviour {

    private AgentBroker myAgent;
    private int State;
    private BrokerAuction ActiveAuction;
    private MessageTemplate mt;
    private int NoPropositionOffers;
    

    DutchAuctionBehaviourBroker(AgentBroker agent, BrokerAuction activeAuction, long period)
    {
        super(agent, period);
        myAgent = agent;
        State = 0;
        NoPropositionOffers = 0;
        ActiveAuction = activeAuction;
    }

    @Override
    protected void onTick() {
        
    }

    
}
