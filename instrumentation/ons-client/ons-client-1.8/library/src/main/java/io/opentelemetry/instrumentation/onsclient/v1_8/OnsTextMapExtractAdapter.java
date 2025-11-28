/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import io.opentelemetry.context.propagation.TextMapGetter;
import javax.annotation.Nullable;

enum OnsTextMapExtractAdapter implements TextMapGetter<MessageExt> {
  INSTANCE;

  @Override
  public Iterable<String> keys(MessageExt carrier) {
    return carrier.getProperties().keySet();
  }

  @Nullable
  @Override
  public String get(@Nullable MessageExt carrier, String key) {
    return carrier == null ? null : carrier.getProperties().get(key);
  }
}
