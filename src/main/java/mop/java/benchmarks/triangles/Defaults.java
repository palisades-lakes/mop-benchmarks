package mop.java.benchmarks.triangles;

import mop.java.SystemInfo;
import mop.java.geometry.triangle.*;
import mop.java.geometry.triangle.jts.*;
import mop.java.geometry.triangle.macro.*;
import mop.java.geometry.triangle.shewchuk.*;
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

/** Base for tetrahedra operation benchmarks.
 *
 * <pre>
 * java -cp target\benchmarks.jar mop.java.benchmarks.arithmetic.Base
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-27
 * */

public final class Defaults {

  //--------------------------------------------------------------
  /** conversions from any Triangle2D to other Triangle classes. */

  public static final Triangle2D convertTriangle (final Triangle2D t,
                                                  final String dest) {
    // TODO: lookup method object rather than switch (String)
    return switch (dest) {
      case "TriangleVector2D" -> TriangleVector2D.from(t);
      case "DoubleTriangle2D" ->  DoubleTriangle2D.from(t);
      case "DoubleIntervalTriangle2D" ->  DoubleIntervalTriangle2D.from(t);
      case "BigFloatTriangle2D" ->  BigFloatTriangle2D.from(t);
      case "DIBFTriangle2D" ->  DIBFTriangle2D.from(t);
      case "RationalFloatTriangle2D" ->  RationalFloatTriangle2D.from(t);
      case "DDFast" ->  DDFast.from(t);
      case "DDNormalized" ->  DDNormalized.from(t);
      case "DDSlow" ->  DDSlow.from(t);
//    case "InCircleCC" ->  InCircleCC.from(t);
      case "DoubleNonRobust" ->  DoubleNonRobust.from(t);
      case "InCircleNormalized" ->  InCircleNormalized.from(t);
      case "Adapt" ->  Adapt.from(t);
      case "Exact" ->  Exact.from(t);
      case "ExactCache" ->  ExactCache.from(t);
      case "Fast" ->  Fast.from(t);
      case "Slow" ->  Slow.from(t);
      case "AdaptMacro" ->  AdaptMacro.from(t);
      case "DefaultMacro" ->  DefaultMacro.from(t);
      case "ExactMacro" ->  ExactMacro.from(t);
      case "FastMacro" ->  FastMacro.from(t);
      case "SlowMacro" ->  SlowMacro.from(t);
      default -> throw new UnsupportedOperationException(); }; }

  public static final Triangle2D[]
  convertTriangles (final Triangle2D[] t,
                    final String dest) {
    for (int i=0; i<t.length; i++) {
      t[i] = convertTriangle(t[i],dest); }
    return t;}

  //--------------------------------------------------------------

  private static final DateTimeFormatter DTF =
    DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

  public static final String now () {
    return LocalDateTime.now().format(DTF); }

  //--------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final Options options (final String fileName,
                                       final String includes) {
    final File parent = new File("output");
    parent.mkdirs();
    assert parent.exists();
    final File csv =
      new File(parent,
        fileName
        + "-"  + SystemInfo.model()
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
        "-ea", "-dsa",
        "-Xmn10g",  "-Xms26g", "-Xmx26g",
        "-XX:+UseFMA",
        "--enable-preview",
        "-XX:+UseParallelGC",
        "-Xbatch",
        "-server"
              )
      .forks(3)
      .warmupIterations(3)
      .warmupTime(TimeValue.seconds(30))
      .measurementIterations(3)
      .measurementTime(TimeValue.seconds(30))
      .build(); }

  //--------------------------------------------------------------

  public static final void run (final String fileName,
                                final String includes) {

    try {
      final Runner runner =
        new Runner(Defaults.options(fileName,includes));
      runner.run(); }
    catch (final RunnerException e) {
      throw new RuntimeException(e); } }

  public static final void run (final String includes) {
    run(includes,includes); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
