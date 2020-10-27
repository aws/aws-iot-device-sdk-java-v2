package software.amazon.smithy.eventstreamrpc.java;

import com.squareup.javapoet.JavaFile;

import java.util.function.Consumer;

public interface JavaFileGenerator extends Consumer<Consumer<JavaFile>> {
    /**
     * This is not the logical Java package/subdirectory to generate models in, but rather the subdirectory
     * used in the output directory to separate other output using the same plugin invocation
     * @return
     */
    String getOutputSubdirectory();
}
