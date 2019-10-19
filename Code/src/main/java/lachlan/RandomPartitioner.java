package lachlan;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.Map;
import java.util.Random;

public class RandomPartitioner {
    public static class RandomPairsPartitioner extends Partitioner<Text, IntWritable> {

        public int getPartition(Text wordPair, IntWritable intWritable, int numPartitions) {


            int partitionNumber = new Random().nextInt(numPartitions);

            return partitionNumber;
        }
    }

    public static class RandomStripesPartitioner extends Partitioner<Text, MapWritable> {

        public int getPartition(Text wordPair, MapWritable mapWritable, int numPartitions) {

            int partitionNumber = new Random().nextInt(numPartitions);

            return partitionNumber;
        }
    }
}
