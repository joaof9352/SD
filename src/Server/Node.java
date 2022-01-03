package Server;

public class Node {
    private String origin, destination;
    private Flight flight;

    public Node(Flight f){
        this.flight = f;
        origin = f.getDepartureCity();
        destination = f.getArrivalCity();
    }

    @Override
    public String toString() {
        return origin + " => " + destination + " (" + flight.getTakenPlaces() + "/" + flight.getCapacity() + ")\n";
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setF(Flight f) {
        this.flight = f;
    }
}
