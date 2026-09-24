package alerts.model;

import java.util.List;
import java.util.Set;

/** The fictional dataset from the lecture. Provided infrastructure. */
public final class SampleData {
    private SampleData() { }

    public static List<Subscriber> subscribers() {
        return List.of(
            new Subscriber("S01", "Aki", "Shinjuku-ku", Set.of()),
            new Subscriber("S02", "Ben", "Shibuya-ku", Set.of("Shinjuku-ku")),
            new Subscriber("S03", "Chen", "Meguro-ku", Set.of("Shibuya-ku")),
            new Subscriber("S04", "Emi", "Shinjuku-ku", Set.of("Shinjuku-ku"))
        );
    }

    public static List<String> wards() {
        return List.of("Shinjuku-ku", "Shibuya-ku", "Meguro-ku", "Nakano-ku");
    }
}
