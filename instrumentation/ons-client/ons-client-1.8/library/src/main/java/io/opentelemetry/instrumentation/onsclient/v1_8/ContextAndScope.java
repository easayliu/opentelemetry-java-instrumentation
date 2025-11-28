/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.google.auto.value.AutoValue;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

@AutoValue
abstract class ContextAndScope {

  abstract Context getContext();

  abstract Scope getScope();

  static ContextAndScope create(Context context, Scope scope) {
    return new AutoValue_ContextAndScope(context, scope);
  }

  void close() {
    getScope().close();
  }
}
