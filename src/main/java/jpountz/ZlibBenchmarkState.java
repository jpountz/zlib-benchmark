package jpountz;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ZlibBenchmarkState {

  byte[] uncompressed;
  byte[] compressed;

  byte[] buffer;

  @Param({ "1048576" }) // 1MB
  int uncompressedSize;

  @Param({ "3", "6" })
  int level;

  @Setup(Level.Trial)
  public void setupTrial() throws Exception {
    uncompressed = new byte[uncompressedSize];
    Path path = Paths.get("/home/jpountz/.rally/benchmarks/data/http_logs/documents-191998.json");
    try (InputStream is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
      is.readNBytes(uncompressed, 0, uncompressedSize);
    }

    compressed = new byte[uncompressedSize];
    int numCompressedBytes = ZlibBenchmark.compress(uncompressed, level, compressed);
    compressed = Arrays.copyOf(compressed, numCompressedBytes);

    buffer = new byte[uncompressedSize];
  }

}
