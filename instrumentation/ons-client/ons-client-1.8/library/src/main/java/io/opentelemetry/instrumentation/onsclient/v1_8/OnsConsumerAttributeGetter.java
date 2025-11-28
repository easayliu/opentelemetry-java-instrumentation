/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import io.opentelemetry.instrumentation.api.incubator.semconv.messaging.MessagingAttributesGetter;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

enum OnsConsumerAttributeGetter implements MessagingAttributesGetter<MessageExt, Void> {
  INSTANCE;

  @Override
  public String getSystem(MessageExt messageExt) {
    return "ons";
  }

  @Nullable
  @Override
  public String getDestination(MessageExt messageExt) {
    return messageExt.getTopic();
  }

  @Nullable
  @Override
  public String getDestinationTemplate(MessageExt messageExt) {
    return null;
  }

  @Override
  public boolean isTemporaryDestination(MessageExt messageExt) {
    return false;
  }

  @Override
  public boolean isAnonymousDestination(MessageExt messageExt) {
    return false;
  }

  @Nullable
  @Override
  public String getConversationId(MessageExt messageExt) {
    return null;
  }

  @Nullable
  @Override
  public Long getMessageBodySize(MessageExt messageExt) {
    byte[] body = messageExt.getBody();
    return body == null ? null : (long) body.length;
  }

  @Nullable
  @Override
  public Long getMessageEnvelopeSize(MessageExt messageExt) {
    return null;
  }

  @Nullable
  @Override
  public String getMessageId(MessageExt messageExt, @Nullable Void unused) {
    return messageExt.getMsgId();
  }

  @Nullable
  @Override
  public String getClientId(MessageExt messageExt) {
    return null;
  }

  @Nullable
  @Override
  public Long getBatchMessageCount(MessageExt messageExt, @Nullable Void unused) {
    return null;
  }

  @Override
  public List<String> getMessageHeader(MessageExt messageExt, String name) {
    String value = messageExt.getProperties().get(name);
    if (value != null) {
      return Collections.singletonList(value);
    }
    return Collections.emptyList();
  }
}
