package lachlan;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.Map;

public class PairsPartitioner extends Partitioner<Text, IntWritable> {

    public int getPartition(Text wordPair, IntWritable intWritable, int numPartitions) {
        Map<Character, Integer> char2Integer = PartitionConverter.getCharacterIntegerHashMap();

        int partitionNumber = 0;

        try {
            char firstLetter = wordPair.toString().toLowerCase().charAt(1);


            if (char2Integer.containsKey(firstLetter)) {
                partitionNumber = char2Integer.get(firstLetter) % numPartitions;
            }

        }
        catch (StringIndexOutOfBoundsException e){
            System.out.println(e);
        }
        return partitionNumber;
    }
}
