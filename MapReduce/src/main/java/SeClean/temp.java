package SeClean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class temp {
    public static class TMap extends Mapper<LongWritable, Text, Text, tem> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] data = line.split(",");
            String high_s = data[2];
            String low_s = data[3];
            String ymd = data[4];

            Pattern pattern = Pattern.compile("\\d+"); // 匹配数字的正则表达式
            Matcher matcher_h = pattern.matcher(high_s);
            Matcher matcher_l = pattern.matcher(low_s);

            String high1 = "";
            String low1 = "";

            if (matcher_h.find()) {
                high1 = matcher_h.group();
            }

            if (matcher_l.find()) {
                low1 = matcher_l.group();
            }

            if (!high1.isEmpty() && !low1.isEmpty()) { // 只处理找到匹配数字的情况
                LongWritable high = new LongWritable(Long.parseLong(high1));
                LongWritable low = new LongWritable(Long.parseLong(low1));

                LongWritable differ = new LongWritable(high.get() - low.get());
                Text k2 = new Text(ymd);
                tem v2 = new tem(high, low, differ);
                context.write(k2, v2);
            }
        }
    }


    public static class TReduce extends Reducer<Text, tem, Text, tem> {
        @Override
        protected void reduce(Text key, Iterable<tem> values, Reducer<Text, tem, Text, tem>.Context context) throws IOException, InterruptedException {
            for (tem v2 : values) {
                String high = v2.getHigh().toString() + " \u2103";
                String low = v2.getLow().toString() + " \u2103";
                String differ = v2.getDiffer().toString() + " \u2103";
                context.write(key, new tem(high, low, differ));
            }
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job =Job.getInstance(conf,"temperature");

        job.setJarByClass(temp.class);
        job.setMapperClass(TMap.class);
        job.setReducerClass(TReduce.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(tem.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
