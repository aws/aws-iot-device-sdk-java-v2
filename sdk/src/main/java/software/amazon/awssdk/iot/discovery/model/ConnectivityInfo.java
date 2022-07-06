package software.amazon.awssdk.iot.discovery.model;

import java.util.Objects;

/**
 * Describes a Greengrass core endpoint that a device can connect to
 *
 * API Documentation: https://docs.aws.amazon.com/greengrass/latest/developerguide/gg-discover-api.html
 */
public class ConnectivityInfo {
    private String Id;
    private String HostAddress;
    private String Metadata;
    private Integer PortNumber;

    /**
     * @return identifier associated with this endpoint entry
     */
    public String getId() {
        return Id;
    }

    /**
     *  Sets the identifier associated with this endpoint entry
     * @param id identifier associated with this endpoint entry
     */
    public void setId(String id) {
        this.Id = id;
    }

    /**
     * @return address of the endpoint
     */
    public String getHostAddress() {
        return HostAddress;
    }

    /**
     * Sets the address of the endpoint
     * @param hostAddress address of the endpoint
     */
    public void setHostAddress(String hostAddress) {
        this.HostAddress = hostAddress;
    }

    /**
     * @return additional user-configurable metadata about the connectivity entry
     */
    public String getMetadata() {
        return Metadata;
    }

    /**
     * Sets the additional user-configurable metadata about the connectivity entry
     * @param metadata Additional user-configurable metadata about the connectivity entry
     */
    public void setMetadata(String metadata) {
        this.Metadata = metadata;
    }

    /**
     * @return port of the endpoint
     */
    public Integer getPortNumber() {
        return PortNumber;
    }

    /**
     * Sets the port of the endpoint
     * @param portNumber port of the endpoint
     */
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
