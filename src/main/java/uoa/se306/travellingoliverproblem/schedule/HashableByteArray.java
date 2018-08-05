package uoa.se306.travellingoliverproblem.schedule;

import java.util.Arrays;

public class HashableByteArray {
    private byte[] byteArray;

    public HashableByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HashableByteArray) {
            return Arrays.equals(byteArray, ((HashableByteArray) obj).byteArray);
        } else {
            return false;
        }
    }
}
