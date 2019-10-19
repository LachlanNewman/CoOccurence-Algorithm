package lachlan;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.Map;

public class StripesPartitioner extends Partitioner<Text, MapWritable> {
    public int getPartition(Text text, MapWritable mapWritable, int numPartitions) {

        Map<Character, Integer> char2Integer = PartitionConverter.getCharacterIntegerHashMap();

        int partitionNumber = 0;

        try {
            char firstLetter = text.toString().toLowerCase().charAt(0);


            if (char2Integer.containsKey(firstLetter)) {
                partitionNumber = char2Integer.get(firstLetter) % numPartitions;
            }

        } catch (StringIndexOutOfBoundsException e) {
            System.out.println(e);
        }

        return partitionNumber;

    }
}
