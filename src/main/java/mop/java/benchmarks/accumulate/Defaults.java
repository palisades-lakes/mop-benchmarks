package mop.java.benchmarks.accumulate;

import mop.java.SystemInfo;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark <code>double[]</code> sums.
 *
 * <pre>
 * java -cp target\benchmarks.jar mop.java.Base
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-04
 */

public final class Defaults {

  //--------------------------------------------------------------

  private static final DateTimeFormatter DTF =
    DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

  public static final String now () {
    return LocalDateTime.now().format(DTF);
  }

  //--------------------------------------------------------------

  public static final Options options (final String fileName,
                                       final String includes) {
    final File parent = new File("output");
    parent.mkdirs();
    final File csv =
      new File(parent,
               fileName
                 + "-" + SystemInfo.model()
                 + "-" + now()
                 + ".csv");
    //final File json =
    //  new File(parent, fileName + "-" + now() + ".json");
    return new OptionsBuilder()
      .mode(Mode.AverageTime)
      .timeUnit(TimeUnit.MILLISECONDS)
      .include(includes)
      //.resultFormat(ResultFormatType.JSON)
      //.result(json.getPath())
      .resultFormat(ResultFormatType.CSV)
      .result(csv.getPath())
      .threads(1)
      .shouldFailOnError(true)
      .shouldDoGC(true)
      .jvmArgs(
        "--enable-preview",
        "-Xmx5g", "-Xms5g", "-Xmn2500m",
        "-XX:+UseFMA",
        "--sun-misc-unsafe-memory-access=allow",
        "--illegal-native-access=allow")
      .warmupIterations(3)
      .warmupTime(TimeValue.seconds(24))
      .measurementIterations(4)
      .measurementTime(TimeValue.seconds(20))
      .build();
  }

  //--------------------------------------------------------------

  public static final void run (final String fileName,
                                final String includes) {

    try {
      final Runner runner =
        new Runner(Defaults.options(fileName, includes));
      runner.run();
    }
    catch (final RunnerException e) {
      throw new RuntimeException(e);
    }
  }

  public static final void run (final String includes) {
    run(includes, includes);
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
