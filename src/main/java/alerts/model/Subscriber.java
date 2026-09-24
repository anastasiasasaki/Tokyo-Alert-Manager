package alerts.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** A subscriber is identified by ID, not by name. Provided infrastructure. */
public final class Subscriber {
    private final String id;
    private final String name;
    private final String homeWard;
    private final Set<String> followedWards;

    public Subscriber(String id, String name, String homeWard, Set<String> followedWards) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.homeWard = requireText(homeWard, "homeWard");
        Objects.requireNonNull(followedWards, "followedWards");
        LinkedHashSet<String> wards = new LinkedHashSet<>();
        for (String ward : followedWards) {
            wards.add(requireText(ward, "followed ward"));
        }
        this.followedWards = Collections.unmodifiableSet(wards);
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getHomeWard() { return homeWard; }
    public Set<String> getFollowedWards() { return followedWards; }
}
