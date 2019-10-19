package lachlan;

public class HeapManagment {

    private static final Runtime runtime = Runtime.getRuntime();
    private static final int mb = 1024*1024;

    private static void printMemory(){
        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " +
                Runtime.getRuntime().freeMemory()/mb);

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/mb));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " +
                Runtime.getRuntime().totalMemory()/mb);

    }

    public static boolean excessiveConsumed(long minMemoryRequired){
//        printMemory();
        long freeMemory = runtime.freeMemory() / mb;
        return freeMemory < minMemoryRequired;
    }

    public static boolean fractionConsumed(long heapFraction){
//        printMemory();
        long maxMemory = runtime.maxMemory() / mb;
        long freeMemory = runtime.freeMemory() / mb;
        long maxMemoryFraction = maxMemory / heapFraction;
        return freeMemory < maxMemoryFraction;
    }
}
