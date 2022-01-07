package Server;

import Server.Exceptions.FlightIsFullException;
import Server.Exceptions.NoFlightFoundException;
import javafx.util.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Company {

    List<Flight> recurrentFlights = null;
    Map<LocalDate, Day> flightCalendar = null;
    Map<String, User> userMap = null;
    List<Reservation> reservationList = null;
    ReentrantLock lock = new ReentrantLock();
    //Para segurança, quando há um login, gerar um inteiro random, enviado para o cliente e cada request tem que trazer esse inteiro

    public Company(){
        recurrentFlights = new ArrayList<>();
        flightCalendar = new HashMap<>();
        userMap = new HashMap<>();
        reservationList = new ArrayList<>();
    }

    /* Authentication */
    public void signUp(String username, String password) {
        if (userMap.containsKey(username)) {
            //Username já existe
        } else {
            userMap.put(username, new User(username, password));
        }
    }

    public void signIn(String username, String password){
        if (userMap.containsKey(username)){
            if(userMap.get(username).checkPassword(password)){
                //User validado
            }else{
                //Password incorreta
            }
        } else {
            //User não encontrado
        }
    }

    /* Admin */

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


    /* Client */

    // (("Porto","Lisboa"),21-01-2022) -> Precisamos dum lock
    public boolean makeReservation(List<Pair<Pair<String,String>,LocalDate>> flights) {

        boolean flightsExist = true;

        //Verifica que todos os voos existem e que tem lugares
        for(Pair<Pair<String,String>,LocalDate> flight : flights ){
            if(!flightIsRecurrent(flight.getKey().getKey(),flight.getKey().getValue())
                && (!flightCalendar.containsKey(flight.getValue())
                    || !flightCalendar.get(flight.getValue()).flightFull(flight.getKey().getKey(),flight.getKey().getValue()))){
                flightsExist = false;
            }
        }

        //Se 1 ou + voos não existir, retorna um erro para o cliente
        if(!flightsExist){
            //Enviar erro para cliente
            return false;
        }

        for(Pair<Pair<String,String>,LocalDate> flight : flights ){
            if(!flightCalendar.containsKey(flight.getValue())){
                flightCalendar.put(flight.getValue(),new Day(flight.getValue(), recurrentFlights));
            }
            flightCalendar.get(flight.getValue()).buyTicket(flight.getKey().getKey(),flight.getKey().getValue());
        }

        
        return true;
    }
}
