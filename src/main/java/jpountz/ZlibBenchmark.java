package jpountz;

import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ZlibBenchmark {

  static int compress(byte[] input, int level, byte[] output) throws Exception {
    Deflater deflater = new Deflater(level, true);
    deflater.reset();
    deflater.setInput(input);
    deflater.finish();

    int totalCount = 0;
    for (; ; ) {
      final int count = deflater.deflate(output, totalCount, output.length - totalCount);
      totalCount += count;
      if (deflater.finished()) {
        break;
      } else {
        throw new Error("Output buffer too small");
      }
    }
    return totalCount;
  }

  static int decompress(byte[] input, byte[] output) throws Exception {
    final Inflater inflater = new Inflater(true);
    inflater.setInput(input, 0, input.length);
    int origLength = inflater.inflate(output, 0, output.length);
    if (origLength != output.length) {
      throw new Error();
    }
    return origLength;
  }

  @Benchmark
  public void compress(ZlibBenchmarkState state, Blackhole bh) throws Exception {
    int compressedLength = compress(state.uncompressed, state.level, state.buffer);
    bh.consume(compressedLength);
    bh.consume(state.buffer);
  }

  @Benchmark
  public void decompress(ZlibBenchmarkState state, Blackhole bh) throws Exception {
    int uncompressedLength = decompress(state.compressed, state.buffer);
    bh.consume(uncompressedLength);
    bh.consume(state.buffer);
  }

}
