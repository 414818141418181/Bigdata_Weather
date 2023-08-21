package clean;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Sun implements Writable {
    String sunrise;
    String sunset;

    public Sun() {
        // 无参构造函数
    }

    public Sun(String sunrise, String sunset) {
        this.sunrise =sunrise;
        this.sunset = sunset;
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(sunrise);
        dataOutput.writeUTF(sunset);
    }
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        sunrise = dataInput.readUTF();
        sunset = dataInput.readUTF();
    }
    public String toString() {
        return sunrise+ "\t" +sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }
}
