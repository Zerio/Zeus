package com.ypyproductions.location;

/**
 * A {@link LocationListenerPolicy} that will change based on how long the user
 * has been stationary. This policy will dictate a policy based on a min, max
 * and idle time. The policy will dictate an interval bounded by min and max,
 * and is half of the idle time.
 * 
 * @author DoBao
 */
public class AdaptiveLocationListenerPolicy implements LocationListenerPolicy {

  private final long minInterval;
  private final long maxInterval;
  private final int minDistance;

  // The time the user has been idle at the current location, in milliseconds.
  private long idleTime;

  /**
   * Creates a policy that will be bounded by the given minInterval and
   * maxInterval.
   * 
   * @param minInterval the smallest interval this policy will dictate, in
   *          milliseconds
   * @param maxInterval the largest interval this policy will dictate, in
   *          milliseconds
   * @param minDistance the minimum distance in meters
   */
  public AdaptiveLocationListenerPolicy(long minInterval, long maxInterval, int minDistance) {
    this.minInterval = minInterval;
    this.maxInterval = maxInterval;
    this.minDistance = minDistance;
  }

  /*
   * Returns an interval half of the idle time, but bounded by minInteval and
   * maxInterval.
   */
  @Override
  public long getDesiredPollingInterval() {
    long desiredInterval = idleTime / 2;
    // Round to second to avoid setting the interval too often
    desiredInterval = (desiredInterval / 1000) * 1000;
    return Math.max(Math.min(maxInterval, desiredInterval), minInterval);
  }

  @Override
  public int getMinDistance() {
    return minDistance;
  }

  @Override
  public void updateIdleTime(long newIdleTime) {
    idleTime = newIdleTime;
  }
}
