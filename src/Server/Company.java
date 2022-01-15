package Server;

import Server.Exceptions.DayClosedException;
import Server.Exceptions.FlightIsFullException;
import Server.Exceptions.ImpossibleReservationException;
import Server.Exceptions.NoFlightFoundException;
import Server.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Company {

    List<Flight> recurrentFlights = null;
    Map<LocalDate, Day> flightCalendar = null;
    List<Reservation> reservationList = null;
    ReentrantLock lock = new ReentrantLock();
    //Para segurança, quando há um login, gerar um inteiro random, enviado para o cliente e cada request tem que trazer esse inteiro

    public Company(){
        recurrentFlights = new ArrayList<>();
        flightCalendar = new HashMap<>();
        reservationList = new ArrayList<>();
    }

    /* Admin */

    public void addNewRecurrentFlight(String code, String departureCity, String arrivalCity, int capacity){
        Flight f = new Flight(code,departureCity,arrivalCity,capacity,0);
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
    public int makeReservation(String username, List<Pair<String,String>> flights, LocalDate start, LocalDate end) throws ImpossibleReservationException, NoFlightFoundException, DayClosedException {

        LocalDate actual = start;
        // [(("Porto","Lisboa"),23-10-2021),...]
        List<Pair<Pair<String,String>,LocalDate>> reservations = new ArrayList<>();

        /* Estratégia:
            1. Ir a todos os voos e testar a primeira data. Se der, ok! Se não der: ciclo que testa todas as datas possíveis entre as dadas. Se der, ok, se não der, retorna erro.
                -> A cada interação do ciclo que funcionar, adicionar a uma lista o voo e a data a que terá de ser comprado
         */

        for(Pair<String,String> flight : flights ){

            //Se o voo não existe, então não continuamos
            if(!flightIsRecurrent(flight.getKey(),flight.getValue())){
                System.out.println("Não é recurrent!");
                throw new NoFlightFoundException();
            }
            boolean flightReserved = false;
            while(!flightReserved && actual.isBefore(end) ){
                //Se não existir o Dia ou o Dia estiver aberto seguimos, caso contrário vemos o dia seguinte
                if(!flightCalendar.containsKey(actual) || flightCalendar.get(actual).isOpen()){
                    //Se o voo não estiver cheio, adicionamos aos voos a reservar, caso contrário vemos o dia seguinte
                    if(!flightCalendar.get(actual).flightFull(flight.getKey(),flight.getValue())){
                        reservations.add(new Pair(new Pair(flight.getKey(),flight.getValue()),actual));
                    } else {
                        System.out.println("+1 dia pq voo cheio");
                        actual.plusDays(1);
                    }
                } else {
                    System.out.println("+1 dia pq dia está fechado ou flightCalendar containsKey");
                    actual.plusDays(1);
                }
            }
            //Se o dia atual já é depois do último dia possível de reserva para o cliente, então a reserva é impossível
            if(!actual.isBefore(end)) {
                System.out.println("passou dia atual");
                throw new ImpossibleReservationException();
            }
        }

        for(Pair<Pair<String,String>,LocalDate> flight : reservations ){
            if(!flightCalendar.containsKey(flight.getValue())){
                flightCalendar.put(flight.getValue(),new Day(flight.getValue(), recurrentFlights));
            }
            flightCalendar.get(flight.getValue()).buyTicket(flight.getKey().getKey(),flight.getKey().getValue());
        }
        System.out.println("Eu estou aqui!");
        reservationList.add(new Reservation(username,reservations));
        return reservationList.size()-1;
    }
}
