package Server;

import java.time.LocalDate;

public class test {

    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2022,01,03);
        Day d = new Day(date);
        d.addFlight(new Flight("Porto", "Lisboa", 245));
        d.addFlight(new Flight("Porto", "Faro", 290));
        d.addFlight(new Flight("Lisboa", "Faro", 130));
        d.addFlight(new Flight("Faro","Lisboa",120));
        System.out.println(d.toString());
    }
}
