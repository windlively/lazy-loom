package lucky.baijunhan.datamock.tools;

import lombok.Builder;

@Builder
public class Address {

    String province;

    String city;

    String address;

    public String getAddress() {
        return address;
    }
}
