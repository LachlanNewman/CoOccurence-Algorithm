package lachlan;

import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;

public class Pairs {
    public static class PairsMapperIMCB extends Mapper<Text, ArchiveReader, Text, IntWritable> {

        private static final int memoryRequired = 10;
        private static final IntWritable ONE = new IntWritable(1);
        HashMap<Text, IntWritable> assMap = new HashMap<>();

        public void map(Text key, ArchiveReader value, Context context) throws IOException, InterruptedException {

            int windowSize = context.getConfiguration().getInt("neighbors", 2);

            for (ArchiveRecord r : value) {

                byte[] rawData = IOUtils.toByteArray(r, r.available());
                String content = new String(rawData);
                String[] words = content.split("\\s+");


                for (int i = 0; i < words.length; i++) {

                    if (words[i].isEmpty()) continue;

                    int start = (i - windowSize < 0) ? 0 : i - windowSize;
                    int end = (i + windowSize >= words.length) ? words.length - 1 : i + windowSize;
                    for (int j = start; j <= end; j++) {
                        if (j == i) continue;
                        if (words[j].isEmpty()) continue;
                        String wordPair = "(" + words[i] + "," + words[j] + ")";
                        Text wordPairKey = new Text(wordPair);

                        combineMaps(wordPairKey);

                    }

                    if (HeapManagment.excessiveConsumed(memoryRequired)) inMapCombine(context);
                }

            }

            inMapCombine(context);

        }

        private void inMapCombine(Context context) throws IOException, InterruptedException {
            Set<Text> assMapKeys = assMap.keySet();
            for (Text key : assMapKeys) {
                context.write(key, assMap.get(key));
            }
            assMap.clear();
        }

        private void combineMaps(Text wordPairKey) {
            if (assMap.containsKey(wordPairKey)) {
                IntWritable count = assMap.get(wordPairKey);
                count.set(count.get() + 1);
                assMap.put(wordPairKey, count);
            } else {
                assMap.put(wordPairKey, ONE);
            }
        }

    }

    public static class PairsMapperTextFile extends Mapper<LongWritable, Text, Text, IntWritable> {

        private static final IntWritable ONE = new IntWritable(1);

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            int neighbors = context.getConfiguration().getInt("neighbors", 2);

            String[] words = value.toString().split(",|\\s+");

            for (int i = 0; i < words.length; i++) {

                if (words[i].isEmpty()) continue;

                int start = (i - neighbors < 0) ? 0 : i - neighbors;
                int end = (i + neighbors >= words.length) ? words.length - 1 : i + neighbors;
                for (int j = start; j <= end; j++) {
                    if (j == i) continue;
                    if (words[j].isEmpty()) continue;
                    String wordPair = "(" + words[i] + "," + words[j] + ")";
                    context.write(new Text(wordPair), ONE);
                }
            }

        }

    }

    public static class TokenizerMapper extends Mapper<Text, ArchiveReader, Text, IntWritable> {

        private static final IntWritable ONE = new IntWritable(1);

        public void map(Text key, ArchiveReader value, Context context) throws IOException, InterruptedException {

            int windowSize = context.getConfiguration().getInt("neighbors", 2);

            for (ArchiveRecord r : value) {

                byte[] rawData = IOUtils.toByteArray(r, r.available());
                String content = new String(rawData);
                String[] words = content.split("\\s+");


                for (int i = 0; i < words.length; i++) {

                    if (words[i].isEmpty()) continue;

                    int start = (i - windowSize < 0) ? 0 : i - windowSize;
                    int end = (i + windowSize >= words.length) ? words.length - 1 : i + windowSize;
                    for (int j = start; j <= end; j++) {
                        if (j == i) continue;
                        if (words[j].isEmpty()) continue;
                        String wordPair = "(" + words[i] + "," + words[j] + ")";
                        context.write(new Text(wordPair), ONE);
                    }
                }

            }

        }

    }


    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    private static void deletePreviousOutput(String outputPath, Configuration configuration) throws IOException {
        FileSystem fs = FileSystem.newInstance(configuration);
        if (fs.exists(new Path(outputPath))) {
            fs.delete(new Path(outputPath), true);
        }
    }

    public static void main(String[] args) throws Exception {

        final String TYPE = "Pairs";

        String inputPath = args[0];
        String outputPath = args[1];
        String reducers = args[2];
        boolean inMapCombining = Boolean.parseBoolean(args[3]);
        boolean textFile = Boolean.parseBoolean(args[4]);

        String jobName = TYPE + " Reducers " + reducers;

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(Pairs.class);
        job.setInputFormatClass(WARCFileInputFormat.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setPartitionerClass(PairsPartitioner.class);
        job.setNumReduceTasks(Integer.parseInt(reducers));

        if (inMapCombining) {
            job.setMapperClass(PairsMapperIMCB.class);
        } else {
            job.setMapperClass(TokenizerMapper.class);
        }

        if(textFile){
            job.setMapperClass(PairsMapperTextFile.class);
            job.setInputFormatClass(TextInputFormat.class);
            job.setPartitionerClass(RandomPartitioner.RandomPairsPartitioner.class);
        }

        deletePreviousOutput(outputPath, conf);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
