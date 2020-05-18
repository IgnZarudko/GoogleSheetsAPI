package by.ignot.labs.sheetsapi;

import java.util.Arrays;
import java.util.List;

public class StudentRow {
    private String name;
    private String state;
    private String activity;

    public StudentRow(String name, String state, String activity){
        this.name = name;
        this.state = state;
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getActivity() {
        return activity;
    }

    public List<Object> toList(){
        return Arrays.asList(name, state, activity);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("");

        sb.append(name).append(", ")
                .append(state).append(", ")
                .append(activity).append('\n');

        return sb.toString();
    }
}
