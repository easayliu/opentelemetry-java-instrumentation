plugins {
  id("otel.library-instrumentation")
}

dependencies {
  compileOnly("com.aliyun.openservices:ons-client:1.8.4.Final")

  compileOnly("com.google.auto.value:auto-value-annotations")
  annotationProcessor("com.google.auto.value:auto-value")
}
