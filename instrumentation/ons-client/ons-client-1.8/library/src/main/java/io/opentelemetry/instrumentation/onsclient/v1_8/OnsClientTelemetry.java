/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.ConsumeMessageHook;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.SendMessageContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.SendMessageHook;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import java.util.List;

/** Entrypoint for instrumenting ONS producers or consumers. */
public final class OnsClientTelemetry {

  /** Returns a new {@link OnsClientTelemetry} configured with the given {@link OpenTelemetry}. */
  public static OnsClientTelemetry create(OpenTelemetry openTelemetry) {
    return builder(openTelemetry).build();
  }

  /**
   * Returns a new {@link OnsClientTelemetryBuilder} configured with the given {@link OpenTelemetry}.
   */
  public static OnsClientTelemetryBuilder builder(OpenTelemetry openTelemetry) {
    return new OnsClientTelemetryBuilder(openTelemetry);
  }

  private final OnsConsumerInstrumenter onsConsumerInstrumenter;
  private final Instrumenter<SendMessageContext, Void> onsProducerInstrumenter;

  OnsClientTelemetry(
      OpenTelemetry openTelemetry,
      List<String> capturedHeaders,
      boolean captureExperimentalSpanAttributes) {
    onsConsumerInstrumenter =
        OnsInstrumenterFactory.createConsumerInstrumenter(
            openTelemetry, capturedHeaders, captureExperimentalSpanAttributes);
    onsProducerInstrumenter =
        OnsInstrumenterFactory.createProducerInstrumenter(
            openTelemetry, capturedHeaders, captureExperimentalSpanAttributes);
  }

  /**
   * Returns a new {@link ConsumeMessageHook} for use with methods like {@link
   * com.aliyun.openservices.shade.com.alibaba.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl#registerConsumeMessageHook(ConsumeMessageHook)}.
   */
  public ConsumeMessageHook newTracingConsumeMessageHook() {
    return new TracingConsumeMessageHookImpl(onsConsumerInstrumenter);
  }

  /**
   * Returns a new {@link SendMessageHook} for use with methods like {@link
   * com.aliyun.openservices.shade.com.alibaba.rocketmq.client.impl.producer.DefaultMQProducerImpl#registerSendMessageHook(SendMessageHook)}.
   */
  public SendMessageHook newTracingSendMessageHook() {
    return new TracingSendMessageHookImpl(onsProducerInstrumenter);
  }
}
