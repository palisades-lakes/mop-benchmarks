package mop.java.benchmarks.accumulate;

/** <pre>
 * j mop.java.All
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

public final class All {
  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("PartialSums");
    Defaults.run("PartialL1s");
    Defaults.run("PartialL1Distances");
    Defaults.run("TotalSum");
    Defaults.run("TotalL1Norm");
    Defaults.run("TotalL1Distance");
    Defaults.run("PartialDots");
    Defaults.run("PartialL2s");
    Defaults.run("PartialL2Distances");
    Defaults.run("TotalDot");
    Defaults.run("TotalL2Norm");
    Defaults.run("TotalL2Distance");
  } }
