package Server;

import Server.Exceptions.FlightIsFullException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day {

    LocalDate day;
    Map<String,List<Node>> graph = null;

    public Day(LocalDate day){
        this.day = day;
        graph = new HashMap<>();
    }

    public Day(LocalDate day, List<Flight> flightList){
        this.day = day;
        this.graph = new HashMap<>();
        for(Flight f : flightList){
            addFlight(f.clone());
        }
    }

    public void addFlight(Flight f){
        if(!graph.containsKey(f.getDepartureCity())){
            graph.put(f.getDepartureCity(), new ArrayList<>());
        }

        graph.get(f.getDepartureCity()).add(new Node(f));
    }

    public void buyTicket(String departure, String arrival) {
        for(Node n : graph.get(departure)){
            n.getFlight().buyTicket();
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
        StringBuilder sb = new StringBuilder();
        for (String key: graph.keySet()) {
            for(Node n : graph.get(key))
                sb.append(n.toString());
        }
        return sb.toString();
    }
}