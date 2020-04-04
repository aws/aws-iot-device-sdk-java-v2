package software.amazon.awssdk.iot.discovery.model;

import java.util.List;
import java.util.Objects;

public class GGGroup {
    private String GGGroupId;
    private List<GGCore> Cores;
    private List<String> CAs;

    public List<GGCore> getCores() {
        return Cores;
    }

    public void setCores(List<GGCore> cores) {
        this.Cores = cores;
    }

    public List<String> getCAs() {
        return CAs;
    }

    public void setCAs(List<String> CAs) {
        this.CAs = CAs;
    }

    public String getGGGroupId() {
        return GGGroupId;
    }

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
