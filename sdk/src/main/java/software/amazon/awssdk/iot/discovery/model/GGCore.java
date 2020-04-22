package software.amazon.awssdk.iot.discovery.model;

import java.util.List;
import java.util.Objects;

public class GGCore {
    private String thingArn;
    private List<ConnectivityInfo> Connectivity;

    public String getThingArn() {
        return thingArn;
    }

    public void setThingArn(String thingArn) {
        this.thingArn = thingArn;
    }

    public List<ConnectivityInfo> getConnectivity() {
        return Connectivity;
    }

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
