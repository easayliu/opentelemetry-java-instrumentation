/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.opentelemetry.api.OpenTelemetry;
import java.util.ArrayList;
import java.util.List;

/** A builder of {@link OnsClientTelemetry}. */
public final class OnsClientTelemetryBuilder {

  private final OpenTelemetry openTelemetry;
  private List<String> capturedHeaders = new ArrayList<>();
  private boolean captureExperimentalSpanAttributes = false;

  OnsClientTelemetryBuilder(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  /**
   * Configures the messaging headers that will be captured as span attributes.
   *
   * @param capturedHeaders A list of messaging header names.
   */
  @CanIgnoreReturnValue
  public OnsClientTelemetryBuilder setCapturedHeaders(List<String> capturedHeaders) {
    this.capturedHeaders = new ArrayList<>(capturedHeaders);
    return this;
  }

  /**
   * Sets whether experimental attributes should be set to spans. These attributes may be changed or
   * removed in the future, so only enable this if you know you do not require attributes stability
   * across versions.
   */
  @CanIgnoreReturnValue
  public OnsClientTelemetryBuilder setCaptureExperimentalSpanAttributes(
      boolean captureExperimentalSpanAttributes) {
    this.captureExperimentalSpanAttributes = captureExperimentalSpanAttributes;
    return this;
  }

  /**
   * Returns a new {@link OnsClientTelemetry} with the settings of this {@link
   * OnsClientTelemetryBuilder}.
   */
  public OnsClientTelemetry build() {
    return new OnsClientTelemetry(
        openTelemetry, capturedHeaders, captureExperimentalSpanAttributes);
  }
}
