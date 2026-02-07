package br.com.gorillaroxo.sanjy.client.web.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DetectRuntimeMode {

    private static final String NATIVE_IMAGE_PROPERTY = "org.graalvm.nativeimage.imagecode";
    private static final String RUNTIME_MODE_NATIVE = "Native";
    private static final String RUNTIME_MODE_JVM = "JVM";

    private DetectRuntimeMode() {
        throw new IllegalStateException("Utility class");
    }

    public static String detect() {
        final String nativeImageProperty = System.getProperty(NATIVE_IMAGE_PROPERTY);
        return nativeImageProperty != null ? RUNTIME_MODE_NATIVE : RUNTIME_MODE_JVM;
    }

}
