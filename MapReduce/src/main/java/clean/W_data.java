package clean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
public class W_data {
    public static class WMap extends Mapper<LongWritable, Text, Text,Sun>{
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text,Text, Sun>.Context context) throws IOException, InterruptedException {
            String lane = value.toString();
            String[] data = lane.split(",");
            if(!key.toString().equals("0")) {
                String ymd = data[4];
                String sunrise =data[6];
                String sunset = data[7];
                Text k2 =new Text(ymd);
                Sun v2 = new Sun(sunrise, sunset);
                context.write(k2, v2);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "dataclean");

        job.setJarByClass(W_data.class);
        job.setMapperClass(WMap.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Sun.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
