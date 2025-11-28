/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import javax.annotation.Nullable;

enum OnsConsumerExperimentalAttributeExtractor
    implements AttributesExtractor<MessageExt, Void> {
  INSTANCE;

  private static final AttributeKey<String> MESSAGING_ROCKETMQ_MESSAGE_TAG =
      AttributeKey.stringKey("messaging.rocketmq.message.tag");
  private static final AttributeKey<String> MESSAGING_ROCKETMQ_MESSAGE_KEYS =
      AttributeKey.stringKey("messaging.rocketmq.message.keys");
  private static final AttributeKey<Long> MESSAGING_ROCKETMQ_QUEUE_ID =
      AttributeKey.longKey("messaging.rocketmq.queue_id");
  private static final AttributeKey<Long> MESSAGING_ROCKETMQ_QUEUE_OFFSET =
      AttributeKey.longKey("messaging.rocketmq.queue_offset");
  private static final AttributeKey<Long> MESSAGING_ROCKETMQ_BORN_TIMESTAMP =
      AttributeKey.longKey("messaging.rocketmq.born_timestamp");
  private static final AttributeKey<Long> MESSAGING_ROCKETMQ_STORE_TIMESTAMP =
      AttributeKey.longKey("messaging.rocketmq.store_timestamp");
  private static final AttributeKey<Long> MESSAGING_ROCKETMQ_RECONSUME_TIMES =
      AttributeKey.longKey("messaging.rocketmq.reconsume_times");

  @Override
  public void onStart(
      AttributesBuilder attributes, Context parentContext, MessageExt messageExt) {
    String tags = messageExt.getTags();
    if (tags != null) {
      attributes.put(MESSAGING_ROCKETMQ_MESSAGE_TAG, tags);
    }
    String keys = messageExt.getKeys();
    if (keys != null) {
      attributes.put(MESSAGING_ROCKETMQ_MESSAGE_KEYS, keys);
    }
    attributes.put(MESSAGING_ROCKETMQ_QUEUE_ID, messageExt.getQueueId());
    attributes.put(MESSAGING_ROCKETMQ_QUEUE_OFFSET, messageExt.getQueueOffset());
    attributes.put(MESSAGING_ROCKETMQ_BORN_TIMESTAMP, messageExt.getBornTimestamp());
    attributes.put(MESSAGING_ROCKETMQ_STORE_TIMESTAMP, messageExt.getStoreTimestamp());
    attributes.put(MESSAGING_ROCKETMQ_RECONSUME_TIMES, (long) messageExt.getReconsumeTimes());
  }

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      MessageExt messageExt,
      @Nullable Void unused,
      @Nullable Throwable error) {
    // No additional attributes on end
  }
}
