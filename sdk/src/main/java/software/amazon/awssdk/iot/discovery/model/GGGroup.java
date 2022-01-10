package software.amazon.awssdk.iot.discovery.model;

import java.util.List;
import java.util.Objects;

/**
 * Information about a Greengrass group: a structured collection of one or more Grengrass cores
 *
 * API Documentation: https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-discover-api.html
 */
public class GGGroup {
    private String GGGroupId;
    private List<GGCore> Cores;
    private List<String> CAs;

    /**
     * @return list of Greengrass cores belonging to this group
     */
    public List<GGCore> getCores() {
        return Cores;
    }

    /**
     * Sets the list of Greengrass cores belonging to this group
     * @param cores list of Greengrass cores belonging to this group
     */
    public void setCores(List<GGCore> cores) {
        this.Cores = cores;
    }

    /**
     * @return list of certificate authorities (in PEM format) associated with the Greengrass group
     */
    public List<String> getCAs() {
        return CAs;
    }

    /**
     * Sets the list of certificate authorities (in PEM format) associated with the Greengrass group
     * @param CAs list of certificate authorities (in PEM format) associated with the Greengrass group
     */
    public void setCAs(List<String> CAs) {
        this.CAs = CAs;
    }

    /**
     * @return identifier for the Greengrass group
     */
    public String getGGGroupId() {
        return GGGroupId;
    }

    /**
     * Sets the identifier for the Greengrass group
     * @param GGGroupId identifier for the Greengrass group
     */
    public void setGGGroupId(String GGGroupId) {
        this.GGGroupId = GGGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GGGroup ggGroup = (GGGroup) o;
        return Objects.equals(GGGroupId, ggGroup.GGGroupId) &&
                Objects.equals(Cores, ggGroup.Cores) &&
                Objects.equals(CAs, ggGroup.CAs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(GGGroupId, Cores, CAs);
    }
}
