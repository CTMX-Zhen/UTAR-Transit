package my.edu.utar.utartransit;

public class Timetable {

    private String timetable_name;
    private int id;

    Timetable(String timetable_name, int id){
        this.timetable_name = timetable_name;
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimetable_name(String timetable_name) {
        this.timetable_name = timetable_name;
    }

    public int getId() {
        return id;
    }

    public String getTimetable_name() {
        return timetable_name;
    }
}
