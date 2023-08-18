package clean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class W_data {
    public static class WMap extends Mapper<Object, Text, Text, Text> {
        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            String lane = value.toString();
            JSONObject jsonObject = JSON.parseObject(lane);
            if (jsonObject != null && !jsonObject.isEmpty()) {
                String ymd = jsonObject.getString("ymd");
                String high = jsonObject.getString("high");
                String low = jsonObject.getString("low");
                String sunrise = jsonObject.getString("sunrise");
                String sunset = jsonObject.getString("sunset");
                String type = jsonObject.getString("type");

                if (ymd != null) {
                    Text k2 = new Text(ymd);
                    Text v2 = new Text(high + "\t" + low + "\t" + sunrise + "\t" + sunset + "\t" + type);
                    context.write(k2, v2);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "dataclean");

        job.setJarByClass(W_data.class);
        job.setMapperClass(WMap.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
