package nbnl.com.juyeoung.yo.Controller;

import java.util.ArrayList;

import nbnl.com.juyeoung.yo.Model.Place;

public class PlaceData {

    public static String[] placeNameArray = {
            "anniesoda",
            "charles","chris","claire","dale78","dave","david","futurizer","ian","james","joon","kb","kevin","mark","mason","mikeshong","ryan","sniper","wcool","yoon"};

    public static ArrayList<Place> placeList() {
        ArrayList<Place> list = new ArrayList<>();
        for (int i = 0; i < placeNameArray.length; i++) {
            Place place = new Place();
            place.name = placeNameArray[i];
            place.imageName = "borabora";//placeNameArray[i].replaceAll("\\s+", "").toLowerCase();
            if (i == 2 || i == 5) {
                place.isFav = true;
            }
            list.add(place);
        }
        return (list);
    }
}
