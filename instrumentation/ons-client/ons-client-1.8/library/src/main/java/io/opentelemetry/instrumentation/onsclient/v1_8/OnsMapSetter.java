/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.SendMessageContext;
import io.opentelemetry.context.propagation.TextMapSetter;

enum OnsMapSetter implements TextMapSetter<SendMessageContext> {
  INSTANCE;

  @Override
  public void set(SendMessageContext carrier, String key, String value) {
    if (carrier == null) {
      return;
    }
    carrier.getMessage().getProperties().put(key, value);
  }
}
