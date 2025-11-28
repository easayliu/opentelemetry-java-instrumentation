/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import static io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor.constant;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.SendMessageContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.instrumentation.api.incubator.semconv.messaging.MessageOperation;
import io.opentelemetry.instrumentation.api.incubator.semconv.messaging.MessagingAttributesExtractor;
import io.opentelemetry.instrumentation.api.incubator.semconv.messaging.MessagingAttributesGetter;
import io.opentelemetry.instrumentation.api.incubator.semconv.messaging.MessagingSpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanLinksExtractor;
import io.opentelemetry.instrumentation.api.internal.PropagatorBasedSpanLinksExtractor;
import java.util.List;

final class OnsInstrumenterFactory {

  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.ons-client-1.8";

  private static final AttributeKey<String> MESSAGING_OPERATION =
      AttributeKey.stringKey("messaging.operation");
  private static final AttributeKey<String> MESSAGING_SYSTEM =
      AttributeKey.stringKey("messaging.system");

  static Instrumenter<SendMessageContext, Void> createProducerInstrumenter(
      OpenTelemetry openTelemetry,
      List<String> capturedHeaders,
      boolean captureExperimentalSpanAttributes) {

    OnsProducerAttributeGetter getter = OnsProducerAttributeGetter.INSTANCE;
    MessageOperation operation = MessageOperation.PUBLISH;

    InstrumenterBuilder<SendMessageContext, Void> instrumenterBuilder =
        Instrumenter.<SendMessageContext, Void>builder(
                openTelemetry,
                INSTRUMENTATION_NAME,
                MessagingSpanNameExtractor.create(getter, operation))
            .addAttributesExtractor(
                buildMessagingAttributesExtractor(getter, operation, capturedHeaders));
    if (captureExperimentalSpanAttributes) {
      instrumenterBuilder.addAttributesExtractor(
          OnsProducerExperimentalAttributeExtractor.INSTANCE);
    }

    return instrumenterBuilder.buildProducerInstrumenter(OnsMapSetter.INSTANCE);
  }

  static OnsConsumerInstrumenter createConsumerInstrumenter(
      OpenTelemetry openTelemetry,
      List<String> capturedHeaders,
      boolean captureExperimentalSpanAttributes) {

    InstrumenterBuilder<Void, Void> batchReceiveInstrumenterBuilder =
        Instrumenter.<Void, Void>builder(
                openTelemetry, INSTRUMENTATION_NAME, OnsInstrumenterFactory::spanNameOnReceive)
            .addAttributesExtractor(constant(MESSAGING_SYSTEM, "ons"))
            .addAttributesExtractor(constant(MESSAGING_OPERATION, "receive"));

    return new OnsConsumerInstrumenter(
        createProcessInstrumenter(
            openTelemetry, capturedHeaders, captureExperimentalSpanAttributes, false),
        createProcessInstrumenter(
            openTelemetry, capturedHeaders, captureExperimentalSpanAttributes, true),
        batchReceiveInstrumenterBuilder.buildInstrumenter(SpanKindExtractor.alwaysConsumer()));
  }

  private static Instrumenter<MessageExt, Void> createProcessInstrumenter(
      OpenTelemetry openTelemetry,
      List<String> capturedHeaders,
      boolean captureExperimentalSpanAttributes,
      boolean batch) {

    OnsConsumerAttributeGetter getter = OnsConsumerAttributeGetter.INSTANCE;
    MessageOperation operation = MessageOperation.PROCESS;

    InstrumenterBuilder<MessageExt, Void> builder =
        Instrumenter.builder(
            openTelemetry,
            INSTRUMENTATION_NAME,
            MessagingSpanNameExtractor.create(getter, operation));

    builder.addAttributesExtractor(
        buildMessagingAttributesExtractor(getter, operation, capturedHeaders));
    if (captureExperimentalSpanAttributes) {
      builder.addAttributesExtractor(OnsConsumerExperimentalAttributeExtractor.INSTANCE);
    }

    if (batch) {
      SpanLinksExtractor<MessageExt> spanLinksExtractor =
          new PropagatorBasedSpanLinksExtractor<>(
              openTelemetry.getPropagators().getTextMapPropagator(),
              OnsTextMapExtractAdapter.INSTANCE);

      return builder
          .addSpanLinksExtractor(spanLinksExtractor)
          .buildInstrumenter(SpanKindExtractor.alwaysConsumer());
    } else {
      return builder.buildConsumerInstrumenter(OnsTextMapExtractAdapter.INSTANCE);
    }
  }

  private static <T> AttributesExtractor<T, Void> buildMessagingAttributesExtractor(
      MessagingAttributesGetter<T, Void> getter,
      MessageOperation operation,
      List<String> capturedHeaders) {
    return MessagingAttributesExtractor.builder(getter, operation)
        .setCapturedHeaders(capturedHeaders)
        .build();
  }

  private static String spanNameOnReceive(Void unused) {
    return "multiple_sources receive";
  }

  private OnsInstrumenterFactory() {}
}
