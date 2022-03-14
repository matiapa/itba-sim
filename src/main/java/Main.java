import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        // Read input parameters

        FileReader fr = new FileReader("src/main/resources/static.json");
        JSONObject json = (JSONObject) new JSONParser().parse(fr);

        Long L = (Long) json.get("L");
        float rc = ((Number) json.get("Rc")).floatValue();

        Long M = (Long) json.get("M");
        boolean periodicContours = json.get("periodicContours").toString().equals("true");

        JSONArray particlesJson = (JSONArray) json.get("particles");
        Set<Particle> particles = new HashSet<>();

        int id = 0;
        for(Object object : particlesJson) {
            JSONObject particleJson = (JSONObject) object;
            float r = ((Number) particleJson.get("radius")).floatValue();
            float x = (float) Math.random() * L;
            float y = (float) Math.random() * L;
            particles.add(new Particle(id++, x, y, r));
        }

        // Execute Cell Index Method

        Map<Particle, Set<Particle>> particleNeighbours = CIM.findNeighbours(particles, rc, L.intValue(), M.intValue(), periodicContours);
        CIM.outputResult(particles, L.intValue());

        // Print the results

        for(Map.Entry<Particle, Set<Particle>> entry : particleNeighbours.entrySet()) {
            System.out.printf("(%f, %f): ", entry.getKey().getX(), entry.getKey().getY());
            entry.getValue().forEach(p  -> {
                System.out.printf("(%f, %f) ", p.getX(), p.getY());
            });
            System.out.println("");
        }
    }

}
