package software.amazon.smithy.eventstreamrpc.java;

import com.squareup.javapoet.JavaFile;
import software.amazon.smithy.eventstreamrpc.java.client.ServiceClientClassBuilder;
import software.amazon.smithy.model.Model;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Generates a projection
 */
public class ClientGenerator implements JavaFileGenerator {
    private static final Logger LOGGER = Logger.getLogger(ClientGenerator.class.getName());

    private final ServiceCodegenContext context;
    private final Model model;

    public ClientGenerator(final ServiceCodegenContext context, final Model model) {
        this.context = context;
        this.model = model;
    }

    /**
     * Kind of a dumb way of saying "produce Java files here for this service, and don't worry about it"
     * @param javaFileConsumer
     */
    @Override
    public void accept(final Consumer<JavaFile> javaFileConsumer) {
        final ServiceClientClassBuilder serviceClientClassBuilder = new ServiceClientClassBuilder(context);

        //redundant to pass in ServiceShape since the context has it already...
        serviceClientClassBuilder.apply(context.getServiceShape()).stream().forEach(javaFile -> {
            javaFileConsumer.accept(javaFile);
        });
    }

    @Override
    public String getOutputSubdirectory() {
        return "client";
    }
}
