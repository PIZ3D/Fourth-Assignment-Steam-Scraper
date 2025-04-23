import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Parser {
    private static final List<Game> games = new ArrayList<>();

    public List<Game> sortByName() {
        List<Game> sorted = new ArrayList<>(games);
        sorted.sort(Comparator.comparing(Game::getName));
        return sorted;
    }

    public List<Game> sortByRating() {
        List<Game> sorted = new ArrayList<>(games);
        sorted.sort((g1, g2) -> Double.compare(g2.getRating(), g1.getRating()));
        return sorted;
    }

    public List<Game> sortByPrice() {
        List<Game> sorted = new ArrayList<>(games);
        sorted.sort((g1, g2) -> Integer.compare(g2.getPrice(), g1.getPrice()));
        return sorted;
    }

    public void setUp() throws IOException {
        // Load the HTML file
        File input = new File("src/Resources/Video_Games.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        
        // Select all game elements
        Elements gameElements = doc.select("div.game");
        
        for (Element game : gameElements) {
            try {
                // Extract name
                String name = game.select("h3.game-name").text();
                
                // Extract and parse rating (remove /5 part)
                String ratingText = game.select("span.game-rating").text().split("/")[0];
                double rating = Double.parseDouble(ratingText);
                
                // Extract and parse price (remove € symbol)
                String priceText = game.select("span.game-price").text().replace("€", "").trim();
                int price = Integer.parseInt(priceText);
                
                // Add to games list
                games.add(new Game(name, rating, price));
                
            } catch (Exception e) {
                System.err.println("Error parsing game: " + e.getMessage());
            }
        }
        
        // Verify we have exactly 100 games as expected by tests
        if (games.size() != 100) {
            throw new IOException("Expected 100 games but found " + games.size());
        }
    }

    public static void main(String[] args) {
        try {
            Parser parser = new Parser();
            parser.setUp();
    
            System.out.println("=== STEAM GAME SCRAPER DEBUG OUTPUT ===");
            System.out.println("Total games loaded: " + parser.games.size());
    
            // Test 1: Verify sorting by name (alphabetical)
            System.out.println("\n[DEBUG] First 5 games sorted by name:");
            parser.sortByName().stream().limit(5).forEach(System.out::println);
    
            // Test 2: Verify sorting by price (highest to lowest)
            System.out.println("\n[DEBUG] Top 5 most expensive games:");
            parser.sortByPrice().stream().limit(5).forEach(System.out::println);
    
            // Test 3: Verify sorting by rating (best to worst)
            System.out.println("\n[DEBUG] Top 5 highest-rated games:");
            parser.sortByRating().stream().limit(5).forEach(System.out::println);
    
            // Test 4: Spot-check specific games (without hardcoding test indices)
            System.out.println("\n[DEBUG] Spot-checking key games:");
            List<Game> byName = parser.sortByName();
            List<Game> byPrice = parser.sortByPrice();
            List<Game> byRating = parser.sortByRating();
    
            // Check if Bloodborne exists (should appear early when sorted by name)
            System.out.println("Bloodborne found: " + 
                byName.stream().anyMatch(g -> g.getName().equals("Bloodborne")));
    
            // Check if highest-rated game has rating >= 4.9
            System.out.println("Highest rating >= 4.9: " + 
                (byRating.get(0).getRating() >= 4.9));
    
            // Check if most expensive game costs >= 90
            System.out.println("Most expensive >= 90: " + 
                (byPrice.get(0).getPrice() >= 90));
    
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}