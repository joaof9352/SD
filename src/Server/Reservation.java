package Server;

import Server.Pair;

import java.time.LocalDate;
import java.util.List;

public class Reservation {

    String username;
    //[(("Porto","Lisboa"),21/01/2022);(("Lisboa","Porto"),22/01/2022)]
    List<Pair<Pair<String,String>, LocalDate>> flights;

    public Reservation(String username, List<Pair<Pair<String,String>, LocalDate>> flights){
        this.username = username;
        this.flights = flights;
    }

    public boolean ownsReservation(String username){
        return this.username.equals(username);
    }

    public List<Pair<Pair<String,String>, LocalDate>> getFlights() {
        return flights;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(username + ": \n");
        for(Pair<Pair<String,String>, LocalDate> flight : flights){
            sb.append("\t");
            sb.append(flight.getValue());
            sb.append(": ");
            sb.append(flight.getKey().getKey());
            sb.append(" -> ");
            sb.append(flight.getKey().getValue());
            sb.append("\n");
        }

        return sb.toString();
    }
}
