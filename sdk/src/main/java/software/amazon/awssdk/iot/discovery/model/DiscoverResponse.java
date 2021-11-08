package software.amazon.awssdk.iot.discovery.model;

import java.util.List;
import java.util.Objects;

/**
 * Top-level response data for a greengrass discovery request.  Contains a list of available greengrass groups.
 *
 * API Documentation: https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-discover-api.html
 */
public class DiscoverResponse {
    private List<GGGroup> GGGroups;

    /**
     * @return list of discovered Greengrass groups
     */
    public List<GGGroup> getGGGroups() {
        return GGGroups;
    }

    /**
     * Sets the list of discovered Greengrass groups
     * @param GGGroups list of discovered Greengrass groups
     */
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
