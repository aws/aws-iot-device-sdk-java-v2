package software.amazon.awssdk.iot.discovery.model;

import java.util.List;
import java.util.Objects;

public class DiscoverResponse {
    private List<GGGroup> GGGroups;

    public List<GGGroup> getGGGroups() {
        return GGGroups;
    }

    public void setGGGroups(List<GGGroup> GGGroups) {
        this.GGGroups = GGGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscoverResponse that = (DiscoverResponse) o;
        return Objects.equals(GGGroups, that.GGGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(GGGroups);
    }
}
