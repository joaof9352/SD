package Server;

import javafx.util.Pair;

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

    public List<Pair<Pair<String,String>, LocalDate>> getFlights() {
        return flights;
    }


}
