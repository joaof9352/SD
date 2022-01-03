package Server;

import Server.Exceptions.FlightIsFullException;
import Server.Exceptions.NoFlightFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Company {

    List<Flight> recurrentFlights;
    Map<LocalDate, Day> flightCalendar = null;

    public Company(){
        recurrentFlights = new ArrayList<>();
        flightCalendar = new HashMap<>();
    }

    public void addNewRecurrentFlight(String code, String departureCity, String arrivalCity, int capacity, int takenPlaces){
        Flight f = new Flight(code,departureCity,arrivalCity,capacity,takenPlaces);
        recurrentFlights.add(f); //Adiciona aos futuros dias
        //Adiciona aos atuais dias já criados
        for(LocalDate dateN : flightCalendar.keySet()){
            if(dateN.isAfter(LocalDate.now())){
                flightCalendar.get(dateN).addFlight(f.clone());
            }
        }
    }

    public boolean flightIsRecurrent(String departure, String arrival){
        for(Flight f : recurrentFlights){
            if(f.flightExists(departure,arrival)){
                return true;
            }
        }
        return false;
    }

    public boolean buyTicket(LocalDate date, String origin, String destination) {

        if(!flightIsRecurrent(origin,destination)){
            return false;
            //Responder com código de erro de Voo não encontrado
        }

        if(!flightCalendar.containsKey(date))
            flightCalendar.put(date,new Day(date, recurrentFlights));
        try {
            flightCalendar.get(date).buyTicket(origin, destination);
            //Responder com ACK com
        }catch (FlightIsFullException e){
            e.printStackTrace();
            //Responder com código de erro voo cheio
        }
        return true;
    }
}
