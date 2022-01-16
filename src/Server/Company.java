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

    private List<Flight> recurrentFlights = null;
    private Map<LocalDate, Day> flightCalendar = null;
    private List<Reservation> reservationList = null;
    private ReentrantLock lock = new ReentrantLock();



    public Company(){
        recurrentFlights = new ArrayList<>();
        flightCalendar = new HashMap<>();
        reservationList = new ArrayList<>();
    }

    /* Admin */

    public void addNewRecurrentFlight(String code, String departureCity, String arrivalCity, int capacity){
        Flight f = new Flight(code,departureCity,arrivalCity,capacity,0);


        try {
            lock.lock();
            recurrentFlights.add(f); //Adiciona aos futuros dias
            //Adiciona aos atuais dias já criados
            for (LocalDate dateN : flightCalendar.keySet()) {
                if (dateN.isAfter(LocalDate.now())) {
                    flightCalendar.get(dateN).addFlight(f.clone());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean flightIsRecurrent(String departure, String arrival){
        lock.lock();
        try {
            for (Flight f : recurrentFlights) {
                if (f.flightExists(departure, arrival)) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }


    /* Client */

    // (("Porto","Lisboa"),21-01-2022) -> Precisamos dum lock
    public int makeReservation(String username, List<Pair<String,String>> flights, LocalDate start, LocalDate end) throws ImpossibleReservationException, NoFlightFoundException, DayClosedException, InterruptedException, FlightIsFullException {

        LocalDate actual = start;
        // [(("Porto","Lisboa"),23-10-2021),...]
        List<Pair<Pair<String,String>,LocalDate>> reservations = new ArrayList<>();

        /* Estratégia:
            1. Ir a todos os voos e testar a primeira data. Se der, ok! Se não der: ciclo que testa todas as datas possíveis entre as dadas. Se der, ok, se não der, retorna erro.
                -> A cada interação do ciclo que funcionar, adicionar a uma lista o voo e a data a que terá de ser comprado
         */

        try {
            lock.lock();
            for (Pair<String, String> flight : flights) {

                //Se o voo não existe, então não continuamos
                if (!flightIsRecurrent(flight.getKey(), flight.getValue())) {
                    System.out.println("Não é recurrent!");
                    throw new NoFlightFoundException();
                }

                boolean flightReserved = false;
                while (!flightReserved && actual.isBefore(end)) {
                    //Se não existir o Dia ou o Dia estiver aberto seguimos, caso contrário vemos o dia seguinte
                    if (!flightCalendar.containsKey(actual) || flightCalendar.get(actual).isOpen()) {
                        //Se o voo não estiver cheio, adicionamos aos voos a reservar, caso contrário vemos o dia seguinte
                        if (!flightCalendar.containsKey(actual) || !flightCalendar.get(actual).flightFull(flight.getKey(), flight.getValue())) {

                            reservations.add(new Pair(new Pair(flight.getKey(), flight.getValue()), actual));
                            flightReserved = true;

                        } else {

                            actual = actual.plusDays(1);

                        }
                    } else {

                        actual = actual.plusDays(1);
                    }
                }
                //Se o dia atual já é depois do último dia possível de reserva para o cliente, então a reserva é impossível
                if (!actual.isBefore(end)) {
                    System.out.println("passou dia atual");
                    throw new ImpossibleReservationException();
                }
            }

            for (Pair<Pair<String, String>, LocalDate> flight : reservations) {
                if (!flightCalendar.containsKey(flight.getValue())) {
                    flightCalendar.put(flight.getValue(), new Day(flight.getValue(), recurrentFlights, lock));
                }
                flightCalendar.get(flight.getValue()).buyTicket(flight.getKey().getKey(), flight.getKey().getValue());
            }

            reservationList.add(new Reservation(username,reservations,lock));
            return reservationList.size()-1;

        } finally {
            lock.unlock();
        }
    }

    public void closeDay(LocalDate day){

        lock.lock();

        if(!flightCalendar.containsKey(day)){
            flightCalendar.put(day,new Day(day, recurrentFlights, lock));
        }

        Day d = flightCalendar.get(day);

        d.setOpen(false);
    }

    public boolean ownsReservation(String username, int reservationId) {
        try {
            lock.lock();
            if (getReservation(reservationId) == null || getReservation(reservationId).isCanceled())
                return false;
            return reservationList.get(reservationId).ownsReservation(username);
        } finally {
            lock.unlock();
        }
    }

    public Reservation getReservation(int reservationID){
        try {
            lock.lock();
            if (reservationList.get(reservationID) == null || reservationList.get(reservationID).isCanceled())
                return null;
            else
                return reservationList.get(reservationID);
        } finally {
            lock.unlock();
        }
    }

    public void cancelReservation(int reservationID, String username) throws DayClosedException, ImpossibleReservationException {
        lock.lock();
        try {
            if (getReservation(reservationID) != null) {

                if (ownsReservation(username, reservationID) && !getReservation(reservationID).isCanceled()) {

                    List<Pair<Pair<String, String>, LocalDate>> reservations = getReservation(reservationID).getFlights();

                    //Garantir que os dias não estão fechados
                    for (Pair<Pair<String, String>, LocalDate> reserve : reservations) {
                        if (!flightCalendar.get(reserve.getValue()).isOpen()) {
                            throw new DayClosedException();
                        }
                    }

                    //Retirar as reservas
                    for (Pair<Pair<String, String>, LocalDate> reserve : reservations) {
                        flightCalendar.get(reserve.getValue()).refundTicket(reserve.getKey().getKey(), reserve.getKey().getValue());
                        System.out.println("x4");
                    }
                    //setCanceled
                    getReservation(reservationID).setCanceled(true);


                } else {
                    throw new ImpossibleReservationException();
                }
            } else {
                throw new ImpossibleReservationException();
            }
        } finally {
            lock.unlock();
        }
    }

    public String getAllFlights(){
        StringBuilder sb = new StringBuilder();
        for(Flight f : recurrentFlights){
            sb.append(f.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
