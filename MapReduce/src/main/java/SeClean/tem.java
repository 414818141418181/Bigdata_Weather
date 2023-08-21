package SeClean;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class tem implements Writable {
    private LongWritable high;
    private LongWritable low;
    private LongWritable differ;
    private String high_t;
    private String low_t;
    private String differ_t;

    public tem() {

    }

    public tem(LongWritable high, LongWritable low, LongWritable differ) {
        this.high = high;
        this.low = low;
        this.differ = differ;
        this.high_t = "";
        this.low_t = "";
        this.differ_t = "";
    }

    public tem(String high_t, String low_t, String differ_t) {
        this.high_t = high_t;
        this.low_t = low_t;
        this.differ_t = differ_t;
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(high.get());
        dataOutput.writeLong(low.get());
        dataOutput.writeLong(differ.get());
        dataOutput.writeUTF(high_t);
        dataOutput.writeUTF(low_t);
        dataOutput.writeUTF(differ_t);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.high = new LongWritable();
        this.high.readFields(dataInput);
        this.low = new LongWritable();
        this.low.readFields(dataInput);
        this.differ = new LongWritable();
        this.differ.readFields(dataInput);
        this.high_t = dataInput.readUTF();
        this.low_t = dataInput.readUTF();
        this.differ_t = dataInput.readUTF();
    }

    public String toString() {
        return high_t+ "\t" +low_t+ "\t" +differ_t;
    }

    public LongWritable getDiffer() {
        return differ;
    }

    public void setDiffer(LongWritable differ) {
        this.differ = differ;
    }

    public String getHigh_t() {
        return high_t;
    }

    public void setHigh_t(String high_t) {
        this.high_t = high_t;
    }

    public String getLow_t() {
        return low_t;
    }

    public void setLow_t(String low_t) {
        this.low_t = low_t;
    }

    public String getDiffer_t() {
        return differ_t;
    }

    public void setDiffer_t(String differ_t) {
        this.differ_t = differ_t;
    }

    public LongWritable getHigh() {
        return high;
    }

    public void setHigh(LongWritable high) {
        this.high = high;
    }

    public LongWritable getLow() {
        return low;
    }

    public void setLow(LongWritable low) {
        this.low = low;
    }
}
