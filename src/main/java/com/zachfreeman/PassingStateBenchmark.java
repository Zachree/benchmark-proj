package com.zachfreeman;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PassingStateBenchmark {

    @State(Scope.Thread)
    public static class MyState {
        String a, b;
        @Setup(Level.Iteration)
        public void setup() {
            a = "some-val";
            b = "some-val2";
        }

        @TearDown(Level.Iteration)
        public void teardown() {
            a = b = "";
        }
    }

    @Benchmark
    public void benchmark(MyState myState) {
        //do whatever with myState
        String res = myState.a + myState.b;
        //....
    }
}
