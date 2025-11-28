/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.SendMessageContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.Message;
import io.opentelemetry.instrumentation.api.incubator.semconv.messaging.MessagingAttributesGetter;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

enum OnsProducerAttributeGetter implements MessagingAttributesGetter<SendMessageContext, Void> {
  INSTANCE;

  @Override
  public String getSystem(SendMessageContext sendMessageContext) {
    return "ons";
  }

  @Nullable
  @Override
  public String getDestination(SendMessageContext sendMessageContext) {
    Message message = sendMessageContext.getMessage();
    return message == null ? null : message.getTopic();
  }

  @Nullable
  @Override
  public String getDestinationTemplate(SendMessageContext sendMessageContext) {
    return null;
  }

  @Override
  public boolean isTemporaryDestination(SendMessageContext sendMessageContext) {
    return false;
  }

  @Override
  public boolean isAnonymousDestination(SendMessageContext sendMessageContext) {
    return false;
  }

  @Nullable
  @Override
  public String getConversationId(SendMessageContext sendMessageContext) {
    return null;
  }

  @Nullable
  @Override
  public Long getMessageBodySize(SendMessageContext sendMessageContext) {
    Message message = sendMessageContext.getMessage();
    if (message != null && message.getBody() != null) {
      return (long) message.getBody().length;
    }
    return null;
  }

  @Nullable
  @Override
  public Long getMessageEnvelopeSize(SendMessageContext sendMessageContext) {
    return null;
  }

  @Nullable
  @Override
  public String getMessageId(SendMessageContext sendMessageContext, @Nullable Void unused) {
    if (sendMessageContext.getSendResult() != null) {
      return sendMessageContext.getSendResult().getMsgId();
    }
    return null;
  }

  @Nullable
  @Override
  public String getClientId(SendMessageContext sendMessageContext) {
    return null;
  }

  @Nullable
  @Override
  public Long getBatchMessageCount(SendMessageContext sendMessageContext, @Nullable Void unused) {
    return null;
  }

  @Override
  public List<String> getMessageHeader(SendMessageContext sendMessageContext, String name) {
    Message message = sendMessageContext.getMessage();
    if (message != null) {
      String value = message.getProperties().get(name);
      if (value != null) {
        return Collections.singletonList(value);
      }
    }
    return Collections.emptyList();
  }
}
