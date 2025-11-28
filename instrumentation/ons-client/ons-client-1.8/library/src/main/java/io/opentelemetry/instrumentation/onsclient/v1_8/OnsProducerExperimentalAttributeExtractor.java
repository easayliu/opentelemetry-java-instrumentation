/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.SendMessageContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.Message;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import javax.annotation.Nullable;

enum OnsProducerExperimentalAttributeExtractor
    implements AttributesExtractor<SendMessageContext, Void> {
  INSTANCE;

  private static final AttributeKey<String> MESSAGING_ROCKETMQ_MESSAGE_TAG =
      AttributeKey.stringKey("messaging.rocketmq.message.tag");
  private static final AttributeKey<String> MESSAGING_ROCKETMQ_MESSAGE_KEYS =
      AttributeKey.stringKey("messaging.rocketmq.message.keys");
  private static final AttributeKey<String> MESSAGING_ROCKETMQ_BROKER_ADDRESS =
      AttributeKey.stringKey("messaging.rocketmq.broker_address");
  private static final AttributeKey<String> MESSAGING_ROCKETMQ_SEND_RESULT =
      AttributeKey.stringKey("messaging.rocketmq.send_result");

  @Override
  public void onStart(
      AttributesBuilder attributes, Context parentContext, SendMessageContext sendMessageContext) {
    Message message = sendMessageContext.getMessage();
    if (message != null) {
      String tags = message.getTags();
      if (tags != null) {
        attributes.put(MESSAGING_ROCKETMQ_MESSAGE_TAG, tags);
      }
      String keys = message.getKeys();
      if (keys != null) {
        attributes.put(MESSAGING_ROCKETMQ_MESSAGE_KEYS, keys);
      }
    }
    String brokerAddr = sendMessageContext.getBrokerAddr();
    if (brokerAddr != null) {
      attributes.put(MESSAGING_ROCKETMQ_BROKER_ADDRESS, brokerAddr);
    }
  }

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      SendMessageContext sendMessageContext,
      @Nullable Void unused,
      @Nullable Throwable error) {
    if (sendMessageContext.getSendResult() != null) {
      attributes.put(
          MESSAGING_ROCKETMQ_SEND_RESULT,
          sendMessageContext.getSendResult().getSendStatus().name());
    }
  }
}
