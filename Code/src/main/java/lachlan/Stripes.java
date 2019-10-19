package lachlan;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Stripes {

    public static class StripesMapperIMCB extends Mapper<Text, ArchiveReader, Text, MapWritable> {

        private static final int memoryRequired = 10;
        private IntWritable ONE = new IntWritable(1);
        private HashMap<Text, MapWritable> assMap = new HashMap<>();

        @Override
        protected void map(Text key, ArchiveReader archiveReader, Context context) throws IOException, InterruptedException {

            int windowSize = context.getConfiguration().getInt("neighbors", 2);
            System.out.println("reading archive recorrds");
            for (ArchiveRecord r : archiveReader) {

                byte[] rawData = IOUtils.toByteArray(r, r.available());
                String content = new String(rawData);
                String[] words = content.split("\\s+");

                for (int i = 0; i < words.length; i++) {
                    if (words[i].isEmpty()) continue;

                    String word = words[i];
                    Text wordKey = new Text(word);
                    MapWritable outMap = new MapWritable();

                    int start = (i - windowSize < 0) ? 0 : i - windowSize;
                    int end = (i + windowSize >= words.length) ? words.length - 1 : i + windowSize;
                    for (int j = start; j <= end; j++) {
                        if (j == i) continue;
                        if (words[j].isEmpty()) continue;

                        String neighbour = words[j];
                        Text neighbourKey = new Text(neighbour);
                        if (outMap.containsKey(neighbourKey)) {
                            IntWritable count = (IntWritable) outMap.get(neighbourKey);
                            count.set(count.get() + 1);
                            outMap.put(neighbourKey, count);
                        } else {
                            outMap.put(neighbourKey, ONE);
                        }

                    }

                    combineMaps(wordKey, outMap);
                    if (HeapManagment.excessiveConsumed(memoryRequired)) inMapCombine(context);
                }


            }

        }

        private void inMapCombine(Context context) throws IOException, InterruptedException {
            Set<Text> assMapKeys = assMap.keySet();
            for (Text key : assMapKeys) {
                context.write(key, assMap.get(key));
            }
            assMap.clear();
        }

        private void combineMaps(Text wordKey, MapWritable outMap) {
            if (assMap.containsKey(wordKey)) {
                MapWritable assOutMap = assMap.get(wordKey);
                Set<Writable> outMapKeys = outMap.keySet();
                for (Writable outMapKey : outMapKeys) {
                    if (assOutMap.containsKey(outMapKey)) {
                        int outMapVal = ((IntWritable) outMap.get(outMapKey)).get();
                        int assOutMapVal = ((IntWritable) assOutMap.get(outMapKey)).get();
                        int newAssOutMapVal = assOutMapVal + outMapVal;
                        assOutMap.put(outMapKey, new IntWritable(newAssOutMapVal));
                    } else {
                        assOutMap.put(outMapKey, outMap.get(outMapKey));
                    }
                }
            } else {
                assMap.put(wordKey, outMap);
            }
        }
    }

    public static class StripesMapperTextFile extends Mapper<LongWritable, Text, Text, MapWritable> {

        private IntWritable ONE = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            int windowSize = context.getConfiguration().getInt("neighbors", 2);

            String[] words = value.toString().split(",|\\s+");

            for (int i = 0; i < words.length; i++) {
                if (words[i].isEmpty()) continue;

                String word = words[i];
                Text wordKey = new Text(word);
                MapWritable outMap = new MapWritable();

                int start = (i - windowSize < 0) ? 0 : i - windowSize;
                int end = (i + windowSize >= words.length) ? words.length - 1 : i + windowSize;
                for (int j = start; j <= end; j++) {
                    if (j == i) continue;
                    if (words[j].isEmpty()) continue;

                    String neighbour = words[j];
                    Text neighbourKey = new Text(neighbour);
                    if (outMap.containsKey(neighbourKey)) {
                        IntWritable count = (IntWritable) outMap.get(neighbourKey);
                        count.set(count.get() + 1);
                        outMap.put(neighbourKey, count);
                    } else {
                        outMap.put(neighbourKey, ONE);
                    }

                }

                context.write(wordKey, outMap);
            }
        }
    }


    public static class StripesMapper extends Mapper<Text, ArchiveReader, Text, MapWritable> {

        private IntWritable ONE = new IntWritable(1);

        @Override
        protected void map(Text key, ArchiveReader archiveReader, Context context) throws IOException, InterruptedException {

            int windowSize = context.getConfiguration().getInt("neighbors", 2);
            System.out.println("reading archive recorrds");
            for (ArchiveRecord r : archiveReader) {

                byte[] rawData = IOUtils.toByteArray(r, r.available());
                String content = new String(rawData);
                String[] words = content.split("\\s+");

                for (int i = 0; i < words.length; i++) {
                    if (words[i].isEmpty()) continue;

                    String word = words[i];
                    Text wordKey = new Text(word);
                    MapWritable outMap = new MapWritable();

                    int start = (i - windowSize < 0) ? 0 : i - windowSize;
                    int end = (i + windowSize >= words.length) ? words.length - 1 : i + windowSize;
                    for (int j = start; j <= end; j++) {
                        if (j == i) continue;
                        if (words[j].isEmpty()) continue;

                        String neighbour = words[j];
                        Text neighbourKey = new Text(neighbour);
                        if (outMap.containsKey(neighbourKey)) {
                            IntWritable count = (IntWritable) outMap.get(neighbourKey);
                            count.set(count.get() + 1);
                            outMap.put(neighbourKey, count);
                        } else {
                            outMap.put(neighbourKey, ONE);
                        }

                    }

                    context.write(wordKey, outMap);
                }


            }

        }
    }

    public static class StripesReducer extends Reducer<Text, MapWritable, Text, MapWritable> {

        private MapWritable incrementingMap = new MapWritable();

        @Override
        protected void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
            incrementingMap.clear();
            for (MapWritable value : values) {

                addAll(value);
            }
            context.write(key, incrementingMap);
        }

        private void addAll(MapWritable mapWritable) {
            Set<Writable> keys = mapWritable.keySet();
            for (Writable key : keys) {
                IntWritable fromCount = (IntWritable) mapWritable.get(key);
                if (incrementingMap.containsKey(key)) {
                    IntWritable count = (IntWritable) incrementingMap.get(key);
                    count.set(count.get() + fromCount.get());
                } else {
                    incrementingMap.put(key, fromCount);
                }
            }
        }
    }

    private static void deletePreviousOutput(String outputPath, Configuration configuration) throws IOException {
        FileSystem fs = FileSystem.newInstance(configuration);
        if (fs.exists(new Path(outputPath))) {
            fs.delete(new Path(outputPath), true);
        }
    }

    public static void main(String[] args) throws Exception {

        final String TYPE = "Stripes";

        String inputPath = args[0];
        String outputPath = args[1];
        String reducers = args[2];
        boolean inMapCombining = Boolean.parseBoolean(args[3]);
        boolean textFile = Boolean.parseBoolean(args[4]);


        String jobName = TYPE + " Reducers " + reducers;

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(Stripes.class);
        job.setInputFormatClass(WARCFileInputFormat.class);
        job.setReducerClass(StripesReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(MapWritable.class);
        job.setPartitionerClass(StripesPartitioner.class);
        job.setNumReduceTasks(Integer.parseInt(reducers));
        deletePreviousOutput(outputPath, conf);

        if (inMapCombining) {
            job.setMapperClass(StripesMapperIMCB.class);
        } else {
            job.setMapperClass(StripesMapper.class);
        }

        if(textFile){
            job.setMapperClass(StripesMapperTextFile.class);
            job.setInputFormatClass(TextInputFormat.class);
            job.setPartitionerClass(RandomPartitioner.RandomStripesPartitioner.class);
        }

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}