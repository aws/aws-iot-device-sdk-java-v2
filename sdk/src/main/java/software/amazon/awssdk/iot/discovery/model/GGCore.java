package software.amazon.awssdk.iot.discovery.model;

import java.util.List;
import java.util.Objects;

/**
 * Information about a particular Greengrass core within a Greengrass group
 *
 * API Documentation: https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-discover-api.html
 */
public class GGCore {
    private String thingArn;
    private List<ConnectivityInfo> Connectivity;

    /**
     * @return resource name of the IoT thing associated with a Greengrass core
     */
    public String getThingArn() {
        return thingArn;
    }

    /**
     * Sets the resource name of the IoT thing associated with a Greengrass core
     * @param thingArn resource name of the IoT thing associated with a Greengrass core
     */
    public void setThingArn(String thingArn) {
        this.thingArn = thingArn;
    }

    /**
     * @return list of distinct ways to connect to the associated Greengrass core
     */
    public List<ConnectivityInfo> getConnectivity() {
        return Connectivity;
    }

    /**
     * Sets the list of distinct ways to connect to the associated Greengrass core
     * @param connectivity list of distinct ways to connect to the associated Greengrass core
     */
    public void setConnectivity(List<ConnectivityInfo> connectivity) {
        Connectivity = connectivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GGCore ggCore = (GGCore) o;
        return Objects.equals(thingArn, ggCore.thingArn) &&
                Objects.equals(Connectivity, ggCore.Connectivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thingArn, Connectivity);
    }
}
