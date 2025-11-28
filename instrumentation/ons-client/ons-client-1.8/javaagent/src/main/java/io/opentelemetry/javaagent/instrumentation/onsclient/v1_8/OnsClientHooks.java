/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.ConsumeMessageHook;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.SendMessageHook;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.onsclient.v1_8.OnsClientTelemetry;
import io.opentelemetry.javaagent.bootstrap.internal.AgentInstrumentationConfig;
import io.opentelemetry.javaagent.bootstrap.internal.ExperimentalConfig;

public final class OnsClientHooks {

  // suppressing deprecation warning for ExperimentalConfig.get().getMessagingHeaders() which will
  // be removed in the future
  @SuppressWarnings("deprecation")
  private static final OnsClientTelemetry TELEMETRY =
      OnsClientTelemetry.builder(GlobalOpenTelemetry.get())
          .setCapturedHeaders(ExperimentalConfig.get().getMessagingHeaders())
          .setCaptureExperimentalSpanAttributes(
              AgentInstrumentationConfig.get()
                  .getBoolean(
                      "otel.instrumentation.ons-client.experimental-span-attributes", false))
          .build();

  public static final ConsumeMessageHook CONSUME_MESSAGE_HOOK =
      TELEMETRY.newTracingConsumeMessageHook();

  public static final SendMessageHook SEND_MESSAGE_HOOK = TELEMETRY.newTracingSendMessageHook();

  private OnsClientHooks() {}
}
