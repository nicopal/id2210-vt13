package tman.system.peer.tman;

import common.configuration.TManConfiguration;
import common.peer.PeerAddress;
import java.util.ArrayList;

import cyclon.system.peer.cyclon.CyclonSample;
import cyclon.system.peer.cyclon.CyclonSamplePort;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import tman.simulator.snapshot.Snapshot;

public final class TMan extends ComponentDefinition {

    Negative<TManSamplePort> tmanPartnersPort = negative(TManSamplePort.class);
    Positive<CyclonSamplePort> cyclonSamplePort = positive(CyclonSamplePort.class);
    Positive<Network> networkPort = positive(Network.class);
    Positive<Timer> timerPort = positive(Timer.class);
    private long period;
    private PeerAddress self;
    private ArrayList<PeerAddress> tmanPartners;
    private TManConfiguration tmanConfiguration;

    public class TManSchedule extends Timeout {

        public TManSchedule(SchedulePeriodicTimeout request) {
            super(request);
        }

//-------------------------------------------------------------------
        public TManSchedule(ScheduleTimeout request) {
            super(request);
        }
    }

//-------------------------------------------------------------------	
    public TMan() {
        tmanPartners = new ArrayList<PeerAddress>();

        subscribe(handleInit, control);
        subscribe(handleRound, timerPort);
        subscribe(handleCyclonSample, cyclonSamplePort);
        subscribe(handleTManPartnersResponse, networkPort);
        subscribe(handleTManPartnersRequest, networkPort);
    }
//-------------------------------------------------------------------	
    Handler<TManInit> handleInit = new Handler<TManInit>() {
        @Override
        public void handle(TManInit init) {
            self = init.getSelf();
            tmanConfiguration = init.getConfiguration();
            period = tmanConfiguration.getPeriod();

            SchedulePeriodicTimeout rst = new SchedulePeriodicTimeout(period, period);
            rst.setTimeoutEvent(new TManSchedule(rst));
            trigger(rst, timerPort);

        }
    };
//-------------------------------------------------------------------	
    Handler<TManSchedule> handleRound = new Handler<TManSchedule>() {
        @Override
        public void handle(TManSchedule event) {
            Snapshot.updateTManPartners(self, tmanPartners);
            // triggers the active Tman thread
            // Publish sample to connected components
            trigger(new TManSample(tmanPartners), tmanPartnersPort); // return the tman result up to the search component
            
        }
    };
//-------------------------------------------------------------------	
    Handler<CyclonSample> handleCyclonSample = new Handler<CyclonSample>() {
        @Override
        public void handle(CyclonSample event) {
            ArrayList<PeerAddress> cyclonPartners = event.getSample();
            // ACTIVE thread
            // wait delta
            // p <-- selectPeer
            // BUFFER <-- merge
            // BUFFER <-- rank
            // send first m entries of buffer to p: trugger <ExchangeMsg.Request>
        }
    };
//-------------------------------------------------------------------	
    Handler<ExchangeMsg.Request> handleTManPartnersRequest = new Handler<ExchangeMsg.Request>() {
        @Override
        public void handle(ExchangeMsg.Request event) {
            // PASSIVE thread lines 1-5
            // BUFFER <-- merge
            // BUFFER <-- rank
            // send first m entries of buffer to q: trugger <ExchangeMsg.Reponse>
            // view <-- merge(biffeer, view) (View = TmanPartners)
        }
    };
    
    Handler<ExchangeMsg.Response> handleTManPartnersResponse = new Handler<ExchangeMsg.Response>() {
        @Override
        public void handle(ExchangeMsg.Response event) {
            // ACTIVE THREAD
            // "Receive buffer p forn P"
            // view <-- merge (bufferP, view)
        }
    };
    
    // implement rank
    // implement merge
    // implement selectPeer
    
    //Method SELECTPEER selects a
    //random sample among the first w entries in the ordered list
    //given as its second parameter.


}
