package PlataformaVoos;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AuxReservation {

    private String username;
    private List<String> airports;
    private LocalDate dataInicio;
    private LocalDate dataFinal;

    public AuxReservation(String userId, List<String> airports, LocalDate dataInicio, LocalDate dataFinal) {
        this.username = userId;
        this.airports = airports;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
    }

    public List<Pair<String,String>> getFlights(){
        List<Pair<String,String>> flights = new ArrayList<>();

        for(int i = 0; i < airports.size()-1; i++){
            flights.add(new Pair<>(airports.get(i),airports.get(i+1)));
        }

        return flights;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }
}
