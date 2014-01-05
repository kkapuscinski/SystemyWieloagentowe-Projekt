/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Broker;

import FIPA.DateTime;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Date;

/**
 *
 * @author Karol
 */
class AcceptOffersBehaviourBroker extends SimpleBehaviour
{
    private AgentBroker Agent;
    private int State;
    private Date endDate;
    

    AcceptOffersBehaviourBroker(AgentBroker agentBroker) 
    {
        Agent = agentBroker;
        State = 0;
        endDate = new Date(new Date().getTime() + (1000 * 60)); // kiedy kończymy przyjmować oferty
    }

    @Override
    public void action() {
        switch(State)
            {
                case(0):
                    ACLMessage receivedMessage = Agent.blockingReceive(500);
                    if(receivedMessage != null)
                    {
                    }
                    break;
            }
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
