package PlataformaVoos;

import Exceptions.FlightIsFullException;

import java.util.concurrent.locks.ReentrantLock;

public class Flight {

    private String code;
    private String departureCity, arrivalCity;
    private int capacity;
    private int takenPlaces;
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public String toString() {
        return code + ": " + departureCity + " -> " + arrivalCity;
    }

    public Flight(String code, String departureCity, String arrivalCity, int capacity, int takenPlaces) {
        this.code = code;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.capacity = capacity;
        this.takenPlaces = takenPlaces;
    }

    public Flight(Flight f) {
        this.code = f.getCode();
        this.departureCity = f.getDepartureCity();
        this.arrivalCity = f.getArrivalCity();
        this.capacity = f.getCapacity();
        this.takenPlaces = 0;
    }

    public boolean flightExists(String departureCity, String arrivalCity) {
        return departureCity.equals(this.getDepartureCity()) && arrivalCity.equals(this.getArrivalCity());
    }

    public boolean flightFull(){
        try{
            lock.lock();
            return takenPlaces == capacity;
        } finally {
            lock.unlock();
        }
    }

    public void buyTicket() throws FlightIsFullException {
        if (flightFull()) throw new FlightIsFullException();

        try {
            lock.lock();
            this.takenPlaces++;
        } finally {
            lock.unlock();
        }
    }

    public void refundTicket(){
        try{
            lock.lock();
            this.takenPlaces--;
        } finally {
            lock.unlock();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getTakenPlaces() {
        return takenPlaces;
    }

    public void setTakenPlaces(int takenPlaces) {
        this.takenPlaces = takenPlaces;
    }

    public Flight clone(){
        return new Flight(this);
    }
}
