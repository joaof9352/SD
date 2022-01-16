package Server;

import Server.Exceptions.DayClosedException;
import Server.Exceptions.FlightIsFullException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Day {

    LocalDate day;
    Map<String,List<Node>> graph = null;
    private boolean isOpen;
    ReentrantLock lock = new ReentrantLock();
    ReentrantLock mainLock;

    public Day(LocalDate day){
        this.day = day;
        graph = new HashMap<>();
        isOpen = true;
    }

    public Day(LocalDate day, List<Flight> flightList, ReentrantLock mainLock){
        this.day = day;
        this.graph = new HashMap<>();
        for(Flight f : flightList){
            addFlight(f.clone());
        }
        this.isOpen = true;
        this.mainLock = mainLock;
    }

    public void addFlight(Flight f){

        if(!this.isOpen)

        if(!graph.containsKey(f.getDepartureCity())){
            graph.put(f.getDepartureCity(), new ArrayList<>());
        }

        graph.get(f.getDepartureCity()).add(new Node(f));
    }

    public void buyTicket(String departure, String arrival) throws DayClosedException, FlightIsFullException {

        for(Node n : graph.get(departure)){
            if(n.getFlight().getArrivalCity().equals(arrival)) {
                n.getFlight().buyTicket();
                break;
            }
        }
    }

    public void refundTicket(String departure, String arrival) {

        for(Node n : graph.get(departure)){
            if(n.getFlight().getArrivalCity().equals(arrival)) {
                n.getFlight().refundTicket();
                break;
            }
        }
    }


    public boolean flightFull(String origin, String destination){
        boolean flightIsFull = true;
        for(Node n : graph.get(origin)){
            if(n.getDestination().equals(destination))
                flightIsFull = n.getFlight().flightFull();
        }

        return flightIsFull;
    }

    @Override
    public String toString() {

        lock.lock();
        StringBuilder sb = new StringBuilder();
        for (String key: graph.keySet()) {
            for(Node n : graph.get(key))
                sb.append(n.toString());
        }
        lock.unlock();
        return sb.toString();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        lock.lock();
        mainLock.unlock();
        isOpen = open;
        lock.unlock();
    }
}