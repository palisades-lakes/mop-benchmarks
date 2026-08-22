package mop.java.scripts.numbers;

/**
 * The maximum Java array length is JVM dependent.
 * Because it is specified using an <code>int</code>
 * in array constructors, it cannot be more that
 * <code>Integer.MAX_VALUE</code>.
 * But the actual maximum is less than that in the JVMs I have
 * checked, even when there is plenty of memory available.
 * Unfortunately, there does not appear any way
 * to determine the JVM-specific maximum array length
 * at runtime.
 * <br>
 * This script does an adhoc test, but I haven't found any
 * guarantees that the "VM limit" is the same across JVMs of the
 * same type, across different invocations of the same JVM,
 * for arrays of different types,
 * or even stays constant for a running JVM.
 *
 * <pre>
 * j src/scripts/java/mop/java/scripts/numbers/MaxArrayLength.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

public final class MaxArrayLength {

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {

    System.getProperties().list(System.out);
    // java -version ->
    // openjdk version "16.0.1" 2021-04-20
    // OpenJDK Runtime Environment (build 16.0.1+9-24)
    // OpenJDK 64-Bit Server VM (build 16.0.1+9-24, mixed mode, sharing)
    // -Xms29g -Xmx29g -Xmn11g

    final int n = Integer.MAX_VALUE-1; // OutOfMemoryError: Requested array size exceeds VM limit
    //final int n = Integer.MAX_VALUE-2; // works:

    final byte[] words = new byte[n];
    words[n-1] = -1;
    System.out.println((n-1) + " -> " + words[n-1]);
    System.out.println((n-1) + " -> " + Integer.toString(words[n-1],16));
    System.out.println((n-1) + " -> " + Integer.toUnsignedString(words[n-1],16));
    // 2147483644 -> -1
    // 2147483644 -> -1
    // 2147483644 -> ffffffff
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
