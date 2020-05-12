package software.amazon.awssdk.iot.discovery.model;

import java.util.Objects;

public class ConnectivityInfo {
    private String Id;
    private String HostAddress;
    private String Metadata;
    private Integer PortNumber;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getHostAddress() {
        return HostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.HostAddress = hostAddress;
    }

    public String getMetadata() {
        return Metadata;
    }

    public void setMetadata(String metadata) {
        this.Metadata = metadata;
    }

    public Integer getPortNumber() {
        return PortNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.PortNumber = portNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectivityInfo that = (ConnectivityInfo) o;
        return Objects.equals(Id, that.Id) &&
                Objects.equals(HostAddress, that.HostAddress) &&
                Objects.equals(Metadata, that.Metadata) &&
                Objects.equals(PortNumber, that.PortNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, HostAddress, Metadata, PortNumber);
    }
}
