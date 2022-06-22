package com.zachfreeman;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class CustomBench {

    @State(Scope.Benchmark)
    public static class CustomThreadState{
        CustomHashShop chs;

        private final double heavyWriterRatio = 0.8, lightWriterRatio = 0.2;

        @Setup(Level.Trial)
        public void prepare(){
            chs = new CustomHashShop();
        }

        @TearDown(Level.Trial)
        public void shutdown(){
            chs = null;
        }
    }

    @State(Scope.Benchmark)
    public static class PrebuiltThreadState{
        PrebuiltHashShop phs;

        private final double heavyWriterRatio = 0.8, lightWriterRatio = 0.2;

        @Setup(Level.Trial)
        public void prepare(){
            phs = new PrebuiltHashShop();
        }

        @TearDown(Level.Trial)
        public void shutdown(){
            phs = null;
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 3, time = 1)
    @Measurement(iterations = 20, time = 1)
    @Threads(6)
    public void heavyWritersCustom(CustomThreadState cts) {
        cts.chs.simulateShopper(cts.heavyWriterRatio);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 3, time = 1)
    @Measurement(iterations = 20, time = 1)
    @Threads(6)
    public void heavyWritersPrebuilt(PrebuiltThreadState pts) {
        pts.phs.simulateShopper(pts.heavyWriterRatio);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 3, time = 1)
    @Measurement(iterations = 20, time = 1)
    @Threads(6)
    public void lightWritersCustom(CustomThreadState cts) {
        cts.chs.simulateShopper(cts.lightWriterRatio);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 3, time = 1)
    @Measurement(iterations = 20, time = 1)
    @Threads(6)
    public void lightWritersPrebuilt(PrebuiltThreadState pts) {
        pts.phs.simulateShopper(pts.lightWriterRatio);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     *  Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar CustomBench -t 6 -f 1
     *    on school server:
     *    $ java -jar -Djmh.ignoreLock=true target/benchmarks.jar CustomBench -t 6 -f 1
     *    append -rf json to the end of each instruction to create a json of the run for jmh visualizer
     *    (we requested 6 threads, single fork; there are also other options, see -h)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CustomBench.class.getSimpleName())
                .threads(6)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
